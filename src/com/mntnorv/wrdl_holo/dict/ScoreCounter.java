package com.mntnorv.wrdl_holo.dict;

public interface ScoreCounter {
	public int addWordScore (String word);
	public int getWordScore (String word);
	public int getTotalScore();
	public void setTotalScore(int score);
}
