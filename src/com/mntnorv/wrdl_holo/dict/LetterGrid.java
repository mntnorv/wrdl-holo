package com.mntnorv.wrdl_holo.dict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import android.util.Log;

public class LetterGrid {
	private List<Letter> grid;
	
	public LetterGrid (String[] letters, int columns, int rows) {
		if (letters.length != columns*rows) {
			throw new IllegalArgumentException ("Size of letters array must be columns*rows");
		}
		
		grid = new ArrayList<Letter>();
		
		int[] directions = {
				-columns,     // UP
				 columns,     // DOWN
				-1,           // LEFT
				 1,           // RIGHT
				-columns - 1, // UP LEFT
				-columns + 1, // UP RIGHT
				 columns - 1, // DOWN LEFT
				 columns + 1  // DOWN RIGHT
		};
		
		for (int i = 0; i < columns*rows; i++) {
			grid.add(new Letter(letters[i]));
		}
		
		for (int i = 0; i < columns*rows; i++) {
			for (int j = 0; j < 8; j++) {
				int row = i / columns;
				int col = i % columns;
				
				if (col == 0 && (j == 2 || j == 4 || j == 6))
					continue;
				if (col == (columns - 1) && (j == 3 || j == 5 || j == 7))
					continue;
				if (row == 0 && (j == 0 || j == 4 || j == 5))
					continue;
				if (row == (rows - 1) && (j == 1 || j == 6 || j == 7))
					continue;
				
				grid.get(i).bordering.add(grid.get(i+directions[j]));
			}
		}
	}
	
	public List<String> getWordsInGrid (Dictionary dict) {
		List<String> words = new ArrayList<String> ();
		
		iterateWordsRecursive (grid, dict, "", words);
		
		// Deduplicate
		words = new ArrayList<String> (new LinkedHashSet<String>(words));
		
		Collections.sort(words);
		
		Log.d("word", Integer.toString(words.size()));
		
		for (String word: words)
			Log.d ("word", word);
		
		return words;
	}
	
	public int getWordCountInGrid (Dictionary dict) {
		List<String> words = new ArrayList<String> ();
		int count = 0;
		
		iterateWordsRecursive (grid, dict, "", words);
		
		// Deduplicate
		words = new ArrayList<String> (new LinkedHashSet<String>(words));
		
		count = words.size();
		
		return count;
	}
	
	private void iterateWordsRecursive (List<Letter> lGrid, Dictionary dict, String word, List<String> words) {
		if (word.length() <= 8) {
			for (Letter current : lGrid) {
				if (!current.used) {
					if (dict.isAWord(word + current.string)) {
						words.add(word + current.string);
					}
					
					if (dict.searchPrefix(word + current.string)) {
						current.used = true;
						iterateWordsRecursive (current.bordering, dict, word + current.string, words);
						current.used = false;
					}
				}
			}
		}
	}
	
	private class Letter {
		public List<Letter> bordering;
		public String string;
		public boolean used;
		
		public Letter (String str) {
			string = str.toUpperCase();
			used = false;
			bordering = new ArrayList<Letter>();
		}
	}
}