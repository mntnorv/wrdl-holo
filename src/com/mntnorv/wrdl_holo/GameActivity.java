package com.mntnorv.wrdl_holo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.mntnorv.wrdl_holo.util.WrdlScoreCounter;
import com.mntnorv.wrdl_holo.util.WrdlWordChecker;
import com.mntnorv.wrdl_holo.views.FlatProgressBarView;
import com.mntnorv.wrdl_holo.views.TileGridView;
import com.slidingmenu.lib.SlidingMenu;

public class GameActivity extends Activity implements GameStateSource.OnLoadFinishedListener {
	
	// Fields
	private SlidingMenu sideMenu;
	private GameState gameState;
	private WordChecker wordChecker;
	private ScoreCounter scoreCounter;
	private Uri gameStateUri;
	
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
        gameStateUri = Uri.parse(intent.getStringExtra(MenuActivity.GAME_STATE_URI));
        int gameStateId = Integer.parseInt(gameStateUri.getLastPathSegment());
        
        gameStateSource = new GameStateSource(this, getLoaderManager(), getContentResolver());
        gameStateSource.getStateById(gameStateId, this);
        
        // Get views from XML
        grid = (TileGridView)findViewById(R.id.mainTileGrid);
        progressBar = (FlatProgressBarView)findViewById(R.id.progressBar);
        gameScoreLayout = (LinearLayout)findViewById(R.id.gameScoreLayout);
        scoreField = (TextView)gameScoreLayout.findViewById(R.id.totalScoreView);
        wordScoreField = (TextView)gameScoreLayout.findViewById(R.id.wordScoreView);
        wordMenu = (ListView)findViewById(R.id.wordMenu);
        wordMenuEmpty = findViewById(R.id.wordsEmptyImage);
        
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
		initializeViews();
	}
    
    @Override
	protected void onPause() {
    	if (gameState != null) {
    		gameStateSource.updateGameState(gameState);
    	}
    	
		super.onPause();
	}

	private void addGuessedWord(String word) {
    	gameState.addGuessedWord(word);
		wordAdapter.notifyDataSetChanged();
		scoreCounter.addWordScore(word);
    }
    
    private void refreshViews() {
    	progressBar.setProgress(gameState.getGuessedWordCount());
		progressBar.setText(Integer.toString(gameState.getGuessedWordCount()));
		scoreField.setText(Integer.toString(scoreCounter.getTotalScore()));
		wordScoreField.setText("");
		
		if (gameState.getGuessedWordCount() > 0) {
    		wordMenuEmpty.setVisibility(View.GONE);
    	}
    }
    
    private void initializeViews() {
    	wordChecker = new WrdlWordChecker(gameState);
        scoreCounter = new WrdlScoreCounter();
        
        grid.create(gameState.getSize());
        grid.setLetters(gameState.getLetterArray());
        grid.setOnWordChangeListener(wordChangeListener);
        grid.setOnWordSelectedListener(wordSelectedListener);
        
        wordAdapter = new WordArrayAdapter(
        		this, R.layout.word_menu_item, R.id.guessedWordField, R.id.guessedWordScore, gameState.getGuessedWords());
        wordAdapter.setScoreCounter(scoreCounter);
        wordMenu.setAdapter(wordAdapter);
        
        for (String guessedWord: gameState.getGuessedWords()) {
        	scoreCounter.addWordScore(guessedWord);
        }
        
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
        sideMenu.setShadowWidthRes(R.dimen.slidingMenu_shadowWidth);
        sideMenu.setShadowDrawable(R.drawable.shadow_drawable);
        sideMenu.setBehindOffsetRes(R.dimen.slidingMenu_leaveWidth);
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
