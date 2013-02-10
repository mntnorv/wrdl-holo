package com.mntnorv.wrdl_holo.util;

import com.mntnorv.wrdl_holo.GameState;
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
					
				return new Result (state);
			} else {
				return new Result (Result.BAD);
			}
		} else {
			return new Result (Result.EMPTY);
		}
	}
}
