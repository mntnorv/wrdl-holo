package com.mntnorv.wrdl_holo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.dict.LetterGrid;
import com.mntnorv.wrdl_holo.dict.WordChecker;
import com.mntnorv.wrdl_holo.views.FlatProgressBarView;
import com.mntnorv.wrdl_holo.views.TileGridView;
import com.slidingmenu.lib.SlidingMenu;

public class GameActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set main layout
        setContentView(R.layout.activity_game);
        
        // Add SlidingMenu
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.word_menu);
        
        // Get views from XML layout
        final TileGridView grid = (TileGridView)findViewById(R.id.mainTileGrid);
        final FlatProgressBarView progressBar = (FlatProgressBarView)findViewById(R.id.progressBar);
        final LinearLayout gameScoreLayout = (LinearLayout)findViewById(R.id.gameScoreLayout);
        final TextView scoreField = (TextView)gameScoreLayout.findViewById(R.id.totalScoreView);
        final TextView wordScoreField = (TextView)gameScoreLayout.findViewById(R.id.wordScoreView);
        final ListView wordMenu = (ListView)findViewById(R.id.wordMenu);
        
        // Initialize fields
        allWords = new ArrayList<String>();
        guessedWords = new ArrayList<String>();
        
        // Local variables
        final int[] score = {0};
        final String[] letters = StringGenerator.randomString(16);
        
        final ArrayAdapter<String> wordAdapter = new ArrayAdapter<String>(this, R.layout.word_menu_item, guessedWords);
        wordMenu.setAdapter(wordAdapter);
        
        // Set up game
        grid.setLetters(letters);
        grid.setOnWordChangeListener(new TileGridView.OnWordChangeListener() {	
			@Override
			public void onWordChange(String word) {
				WordChecker.Result res = wrdlHoloChecker.checkWord(word);
				if (res.isGood()) {
					if (!res.isGuessed()) {
						wordScoreField.setText("+" + Integer.toString(res.getScore()));
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
				WordChecker.Result res = wrdlHoloChecker.checkWord(word);
				if (res.isGood() && !res.isGuessed()) {
					guessedWords.add(word);
					wordAdapter.notifyDataSetChanged();
					score[0] += res.getScore();
					
					progressBar.setProgress(guessedWords.size());
					progressBar.setText(Integer.toString(guessedWords.size()));
					scoreField.setText(Integer.toString(score[0]));
					wordScoreField.setText("");
				}
			}
		});
        
        Dictionary dict = null;
        
		try {
			dict = new Dictionary(getAssets().open("dict.hex"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        LetterGrid lGrid = new LetterGrid(letters, 4, 4);
        allWords.addAll(lGrid.getWordsInGrid(dict));
        
        progressBar.setMaxProgress(allWords.size());
        scoreField.setText("0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
    
    // Word lists
    private ArrayList<String> allWords = null;
    private ArrayList<String> guessedWords = null;
    
    // Word checker implementation
    private WordChecker wrdlHoloChecker = new WordChecker() {
		@Override
		public Result checkWord(String pWord) {
			if (pWord.length() > 0) {
				if (Collections.binarySearch(allWords, pWord) >= 0) {
					byte state = Result.GOOD;
					
					if (guessedWords.contains(pWord)) {
						state |= Result.GUESSED;
					}
						
					return new Result (state, this.getWordScore(pWord));
				} else {
					return new Result (Result.BAD, 0);
				}
			} else {
				return new Result (Result.EMPTY, 0);
			}
		}

		@Override
		public int getWordScore(String pWord) {
			return pWord.length()*5;
		}
    };
}
