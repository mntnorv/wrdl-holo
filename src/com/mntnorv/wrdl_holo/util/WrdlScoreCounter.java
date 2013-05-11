package com.mntnorv.wrdl_holo.util;

import com.mntnorv.wrdl_holo.dict.ScoreCounter;

public class WrdlScoreCounter implements ScoreCounter {

	private int score;
	
	public WrdlScoreCounter() {
		score = 0;
	}
	
	@Override
	public int addWordScore(String word) {
		int tempScore = calculateWordScore(word);
		score += tempScore;
		return tempScore;
	}

	@Override
	public int getWordScore(String word) {
		return calculateWordScore(word);
	}

	@Override
	public int getTotalScore() {
		return score;
	}
	
	@Override
	public void setTotalScore(int score) {
		this.score = score;
	}

	private int calculateWordScore(String word) {
		int wordLength = word.length();
		int wordScore;
		
		if (wordLength < 3) {
			wordScore = 0;
		} else if (wordLength < 5) {
			wordScore = 1;
		} else if (wordLength < 6) {
			wordScore = 2;
		} else if (wordLength < 7) {
			wordScore = 3;
		} else if (wordLength < 8) {
			wordScore = 5;
		} else {
			wordScore = 11;
		}
		
		return wordScore;
	}
}
