package com.mntnorv.wrdl_holo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.dict.LetterGrid;

public class GameState {
	private int size;
	private String[] letterArray;
	private List<String> allWords;
	private List<String> guessedWords;
	
	/* CONSTRUCTOR */
	public GameState(int size, String[] grid) {
		allWords = new ArrayList<String>();
		guessedWords = new ArrayList<String>();
		
		this.size = size;
		
		// Copy letter array
		letterArray = grid.clone();
	}

	/* METHODS */
	public void findAllWords(Dictionary dict) {
		LetterGrid lGrid = new LetterGrid(letterArray, size, size);
        allWords.addAll(lGrid.getWordsInGrid(dict));
	}
	
	public void addGuessedWord(String word) {
		guessedWords.add(word);
	}
	
	public boolean isGuessed(String word) {
		return guessedWords.contains(word);
	}
	
	public boolean isWordInGrid(String word) {
		return Collections.binarySearch(allWords, word) >= 0;
	}

	/* GETTERS */
	public int getSize() {
		return size;
	}

	public String[] getLetterArray() {
		return letterArray.clone();
	}
	
	public List<String> getAllWords() {
		return allWords;
	}

	public List<String> getGuessedWords() {
		return guessedWords;
	}

	public int getWordCount() {
		return allWords.size();
	}
	
	public int getGuessedWordCount() {
		return guessedWords.size();
	}
}
