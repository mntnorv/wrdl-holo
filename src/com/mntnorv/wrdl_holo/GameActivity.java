package com.mntnorv.wrdl_holo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.dict.LetterGrid;
import com.mntnorv.wrdl_holo.dict.WordChecker;
import com.mntnorv.wrdl_holo.views.TileGridView;

public class GameActivity extends Activity {
	
	private int tileDefaultColor = 0xFF33B5E5;
	private int tileGoodColor = 0xFF99CC00;
	private int indicatorDefaultColor = 0xBB33B5E5;
	private int indicatorGoodColor = 0xBB99CC00;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_game);
        
        allWords = new ArrayList<String>();
        guessedWords = new ArrayList<String>();
        
        final TileGridView grid = (TileGridView)findViewById(R.id.mainTileGrid);
        final TextView wordField = (TextView)findViewById(R.id.currentWordField);
        final TextView allWordsField = (TextView)findViewById(R.id.allWordsField);
        final TextView guessedWordsField = (TextView)findViewById(R.id.guessedWordsField);
        final TextView pointsField = (TextView)findViewById(R.id.pointsField);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.wordProgress);
        
        final int[] score = {0};
        
        final String[] letters = StringGenerator.randomString(16);
        
        grid.setLetters(letters);
        grid.setOnWordChangeListener(new TileGridView.OnWordChangeListener() {	
			@Override
			public void onWordChange(String word) {
				WordChecker.Result res = wrdlHoloChecker.checkWord(word);
				if (res.isGood()) {
					if (!res.isGuessed()) {
						grid.setTileHighlightColor(tileGoodColor);
						grid.setIndicatorColor(indicatorGoodColor);
					} else {
						grid.setTileHighlightColor(tileDefaultColor);
						grid.setIndicatorColor(indicatorDefaultColor);
					}
				} else if (res.isBad()) {
					grid.setTileHighlightColor(tileDefaultColor);
					grid.setIndicatorColor(indicatorDefaultColor);
				}
				
				wordField.setText(word.toUpperCase(Locale.US));
			}
		});
        grid.setOnWordSelectedListener(new TileGridView.OnWordSelectedListener() {
			@Override
			public void onWordSelected(String word) {
				WordChecker.Result res = wrdlHoloChecker.checkWord(word);
				if (res.isGood() && !res.isGuessed()) {
					guessedWords.add(word);
					score[0] += res.getScore();
					
					progressBar.setProgress(guessedWords.size() * progressBar.getMax() / allWords.size());
					guessedWordsField.setText(Integer.toString(guessedWords.size()));
					pointsField.setText(Integer.toString(score[0]));
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
        
        allWordsField.setText(Integer.toString(allWords.size()));
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
