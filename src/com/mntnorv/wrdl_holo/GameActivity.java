package com.mntnorv.wrdl_holo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.dict.IWordChecker;
import com.mntnorv.wrdl_holo.dict.LetterGrid;
import com.mntnorv.wrdl_holo.views.TileGridView;
import com.mntnorv.wrdl_holo.views.WordStatusView;

public class GameActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_game);
        
        allWords = new ArrayList<String>();
        guessedWords = new ArrayList<String>();
        
        final TileGridView grid = (TileGridView)findViewById(R.id.mainTileGrid);
        final EditText wordField = (EditText)findViewById(R.id.currentWordField);
        final WordStatusView wordStatus = (WordStatusView)findViewById(R.id.wordStatusView);
        
        final String[] letters = {
        		"A", "B", "C", "D",
        		"E", "F", "G", "H",
        		"I", "J", "K", "L",
        		"M", "N", "O", "Qu"};
        
        grid.setLetters(letters);
        grid.setOnWordChangeListener(new TileGridView.OnWordChangeListener() {	
			@Override
			public void onWordChange(String word) {
				IWordChecker.Result res = wrdlHoloChecker.checkWord(word);
				wordStatus.setStatus(res);
				wordField.setText(word.toUpperCase());
			}
		});
        
        grid.setOnWordSelectedListener(new TileGridView.OnWordSelectedListener() {
			@Override
			public void onWordSelected(String word) {
				IWordChecker.Result res = wrdlHoloChecker.checkWord(word);
				if (res.isGood() && !res.isGuessed()) {
					guessedWords.add(word);
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
    private IWordChecker wrdlHoloChecker = new IWordChecker() {
		@Override
		public Result checkWord(String pWord) {
			pWord = pWord.toUpperCase();
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
