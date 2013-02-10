package com.mntnorv.wrdl_holo;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.dict.ScoreCounter;
import com.mntnorv.wrdl_holo.dict.WordChecker;
import com.mntnorv.wrdl_holo.util.StringGenerator;
import com.mntnorv.wrdl_holo.util.WrdlScoreCounter;
import com.mntnorv.wrdl_holo.util.WrdlWordChecker;
import com.mntnorv.wrdl_holo.views.FlatProgressBarView;
import com.mntnorv.wrdl_holo.views.TileGridView;
import com.slidingmenu.lib.SlidingMenu;

public class GameActivity extends Activity {
	
	// Fields
	private SlidingMenu menu;
	private GameState gameState;
	private WordChecker wordChecker;
	private ScoreCounter scoreCounter;
	
	private WordArrayAdapter wordAdapter;
	
	// Views
	private TileGridView grid;
	private FlatProgressBarView progressBar;
	private LinearLayout gameScoreLayout;
	private TextView scoreField;
	private TextView wordScoreField;
	private ListView wordMenu;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set main layout
        setContentView(R.layout.activity_game);
        addSlidingMenu();
        
        // Load dictionary
        Dictionary dict = null;
		try {
			dict = new Dictionary(getAssets().open("dict.hex"));
		} catch (IOException e) {
			Log.e("dictionary", "Error laoding dictionary from \"dict.hex\"");
		}
        
        // Create a game
        gameState = new GameState(4, 4, StringGenerator.randomString(4 * 4));
        gameState.findAllWords(dict);
        wordChecker = new WrdlWordChecker(gameState);
        scoreCounter = new WrdlScoreCounter();
        
        // Get views from XML
        grid = (TileGridView)findViewById(R.id.mainTileGrid);
        progressBar = (FlatProgressBarView)findViewById(R.id.progressBar);
        gameScoreLayout = (LinearLayout)findViewById(R.id.gameScoreLayout);
        scoreField = (TextView)gameScoreLayout.findViewById(R.id.totalScoreView);
        wordScoreField = (TextView)gameScoreLayout.findViewById(R.id.wordScoreView);
        wordMenu = (ListView)findViewById(R.id.wordMenu);
        
        // Create and setup grid
        grid.create(gameState.getColumns(), gameState.getRows());
        grid.setLetters(gameState.getLetterArray());
        addGridListeners();
        
        // Create adapter for sliding menu
        wordAdapter = new WordArrayAdapter(
        		this, R.layout.word_menu_item, R.id.guessedWordField, R.id.guessedWordScore, gameState.getGuessedWords());
        wordAdapter.setScoreCounter(scoreCounter);
        wordMenu.setAdapter(wordAdapter);
        
        // Setup other views
        progressBar.setMaxProgress(gameState.getWordCount());
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
		case R.id.menu_words:
			menu.toggle();
		}
    	
		return true;
	}
    
    /**
     * Adds a sliding menu to the right side.
     * Used for displaying guessed words.
     */
    private void addSlidingMenu() {
    	menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.slidingMenu_shadowWidth);
        menu.setBehindOffsetRes(R.dimen.slidingMenu_leaveWidth);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.word_menu);
    }
    
    private void addGridListeners() {
        grid.setOnWordChangeListener(new TileGridView.OnWordChangeListener() {	
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
		});
        
        grid.setOnWordSelectedListener(new TileGridView.OnWordSelectedListener() {
			@Override
			public void onWordSelected(String word) {
				WordChecker.Result res = wordChecker.checkWord(word);
				if (res.isGood() && !res.isGuessed()) {
					gameState.addGuessedWord(word);
					wordAdapter.notifyDataSetChanged();
					scoreCounter.addWordScore(word);
					
					progressBar.setProgress(gameState.getGuessedWordCount());
					progressBar.setText(Integer.toString(gameState.getGuessedWordCount()));
					scoreField.setText(Integer.toString(scoreCounter.getTotalScore()));
					wordScoreField.setText("");
				}
			}
		});
    }
}
