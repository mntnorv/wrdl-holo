package com.mntnorv.wrdl_holo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mntnorv.wrdl_holo.db.GameStateSource;
import com.mntnorv.wrdl_holo.dict.DictionaryProvider;
import com.mntnorv.wrdl_holo.dict.ScoreCounter;
import com.mntnorv.wrdl_holo.dict.WordChecker;
import com.mntnorv.wrdl_holo.util.WrdlWordChecker;
import com.mntnorv.wrdl_holo.views.FlatProgressBarView;
import com.mntnorv.wrdl_holo.views.TileGridView;
import com.slidingmenu.lib.SlidingMenu;

public class GameActivity extends Activity implements GameStateSource.OnLoadFinishedListener {
	
	// Fields
	private SlidingMenu sideMenu;
	private GameState gameState;
	private ScoreCounter scoreCounter;
	private WordChecker wordChecker;
	
	private WordArrayAdapter wordAdapter;
	private GameStateSource gameStateSource;
	
	// Views
	private TileGridView grid;
	private FlatProgressBarView progressBar;
	private LinearLayout gameScoreLayout;
	private TextView scoreField;
	private TextView wordScoreField;
	private ListView wordMenu;
	private View wordMenuEmpty;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Set main layout
        setContentView(R.layout.activity_game);
        addSlidingMenu();
        
        // Get game state
        Intent intent = getIntent();
        int gameStateId = intent.getIntExtra(MenuActivity.GAME_STATE_ID, -1);
        
        gameStateSource = new GameStateSource(this, getLoaderManager(), getContentResolver());
        gameStateSource.getStateById(gameStateId, this);
        
        // Get views from XML
        grid = (TileGridView)findViewById(R.id.game_tile_grid);
        progressBar = (FlatProgressBarView)findViewById(R.id.game_progress_bar);
        gameScoreLayout = (LinearLayout)findViewById(R.id.game_score_layout);
        scoreField = (TextView)gameScoreLayout.findViewById(R.id.game_score_total);
        wordScoreField = (TextView)gameScoreLayout.findViewById(R.id.game_score_word_score);
        wordMenu = (ListView)findViewById(R.id.word_menu_list);
        wordMenuEmpty = findViewById(R.id.word_menu_empty_image);
        
        scoreField.setText("0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_words:
			sideMenu.toggle();
			break;
		}
    	
		return true;
	}
    
    @Override
    public void onBackPressed() {
    	if(sideMenu.isMenuShowing()) {
    		sideMenu.showContent();
    	} else {
    		super.onBackPressed();
    	}
    }
	
	@Override
	public void onLoadFinished(List<GameState> result) {
		gameState = result.get(0);
		gameState.findAllWords(DictionaryProvider.getDictionary(this));
		gameState.findGuessedWords();
		scoreCounter = gameState.getScoreCounter();
		initializeViews();
	}
    
    @Override
	protected void onPause() {
    	if (gameState != null) {
    		gameStateSource.updateGameState(gameState);
    	}
    	
		super.onPause();
	}
    
    @Override
    protected void onResume() {
    	if (gameState != null) {
    		gameStateSource.getStateById(gameState.getId(), this);
    	}
    	
    	super.onResume();
    }

	private void addGuessedWord(String word) {
    	gameState.addGuessedWord(word);
		wordAdapter.notifyDataSetChanged();
    }
    
    private void refreshViews() {
    	progressBar.setMaxProgress(gameState.getWordCount());
    	progressBar.setProgress(gameState.getGuessedWordCount());
		progressBar.setText(Integer.toString(gameState.getGuessedWordCount()));
		scoreField.setText(Integer.toString(gameState.getScore()));
		wordScoreField.setText("");
		
		if (gameState.getGuessedWordCount() > 0) {
    		wordMenuEmpty.setVisibility(View.GONE);
    	}
    }
    
    private void initializeViews() {
    	wordChecker = new WrdlWordChecker(gameState);
        
        grid.create(gameState.getSize());
        grid.setLetters(gameState.getLetterArray());
        grid.setOnWordChangeListener(wordChangeListener);
        grid.setOnWordSelectedListener(wordSelectedListener);
        
        wordAdapter = new WordArrayAdapter(
        		this, R.layout.word_menu_item, R.id.word_menu_item_word, R.id.word_menu_item_score, gameState.getGuessedWords());
        wordAdapter.setScoreCounter(scoreCounter);
        wordMenu.setAdapter(wordAdapter);
        
        refreshViews();
    }
    
    /**
     * Adds a sliding menu to the right side.
     * Used for displaying guessed words.
     */
    private void addSlidingMenu() {
    	sideMenu = new SlidingMenu(this);
        sideMenu.setMode(SlidingMenu.RIGHT);
        sideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        sideMenu.setShadowWidthRes(R.dimen.sliding_menu_shadow_width);
        sideMenu.setShadowDrawable(R.drawable.shadow_drawable);
        sideMenu.setBehindOffsetRes(R.dimen.sliding_menu_leave_width);
        sideMenu.setFadeDegree(0.35f);
        sideMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        sideMenu.setMenu(R.layout.word_menu);
    }
    
    private TileGridView.OnWordChangeListener wordChangeListener = new TileGridView.OnWordChangeListener() {	
		@Override
		public void onWordChange(String word) {
			WordChecker.Result res = wordChecker.checkWord(word);
			if (res.isGood()) {
				if (!res.isGuessed()) {
					wordScoreField.setText("+" + Integer.toString(scoreCounter.getWordScore(word)));
				} else {
					wordScoreField.setText("");
				}
			} else if (res.isBad()) {
				wordScoreField.setText("");
			}
		}
	};
	
	private TileGridView.OnWordSelectedListener wordSelectedListener = new TileGridView.OnWordSelectedListener() {
		@Override
		public void onWordSelected(String word) {
			WordChecker.Result res = wordChecker.checkWord(word);
			if (res.isGood() && !res.isGuessed()) {
				addGuessedWord(word);
				refreshViews();
			}
		}
	};
}
