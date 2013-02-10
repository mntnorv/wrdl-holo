package com.mntnorv.wrdl_holo;

import com.mntnorv.wrdl_holo.dict.WordChecker;

public class WrdlWordChecker implements WordChecker {
	private GameState gameState;
	
	public WrdlWordChecker(GameState gameState) {
		this.gameState = gameState;
	}
	
	@Override
	public Result checkWord(String pWord) {
		if (pWord.length() > 0) {
			if (gameState.isWordInGrid(pWord)) {
				byte state = Result.GOOD;
				
				if (gameState.isGuessed(pWord)) {
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
}
