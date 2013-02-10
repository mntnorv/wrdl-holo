package com.mntnorv.wrdl_holo.dict;

public interface ScoreCounter {
	public void addWordScore (String word);
	public int getWordScore (String word);
	public int getTotalScore();
}
