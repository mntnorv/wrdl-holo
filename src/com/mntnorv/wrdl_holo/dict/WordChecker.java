package com.mntnorv.wrdl_holo.dict;

public interface WordChecker {
	public Result checkWord (String pWord);
	public int getWordScore (String pWord);
	
	public class Result {
		private int score;
		private byte state;
		
		public static final byte EMPTY   = (byte) 0;
		public static final byte GOOD    = (byte) 1;
		public static final byte BAD     = (byte) 2;
		public static final byte GUESSED = (byte) 4;
		
		public Result () {
			state = EMPTY;
			score = 0;
		}
		
		public Result (byte pState, int pScore) {
			state = pState;
			score = pScore;
		}
		
		public boolean isGood () {
			return (state | GOOD) == state;
		}
		
		public boolean isBad () {
			return (state | BAD) == state;
		}
		
		public boolean isGuessed () {
			return (state | GUESSED) == state;
		}
		
		public boolean isEmpty () {
			return state == EMPTY;
		}
		
		public int getScore () {
			return score;
		}
	}
}
