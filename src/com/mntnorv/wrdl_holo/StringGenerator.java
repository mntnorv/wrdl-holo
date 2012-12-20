package com.mntnorv.wrdl_holo;

import java.util.Random;

public class StringGenerator {
	private static final String[] VOWELS = {"A", "E", "I", "O", "U"};
	private static final String[] CONSONANTS = {"B", "C", "D", "F", "G", "H", "J", "K",
		"L", "M", "N", "P", "Qu", "R", "S", "T", "V", "W", "X", "Y", "Z"};
	
	/**
	 * Generates a random string (30% vowels and 70% consonants)
	 * @param pLength - length of the string
	 * @return the generated random string
	 */
	public static String[] randomString (int pLength) {
		return randomStringCustom(VOWELS, CONSONANTS, pLength);
	}
	
	/**
	 * Generates a random string (30% vowels and 70% consonants)
	 * @param pVowels - all possible vowels
	 * @param pConsonants - all possible consonants
	 * @param pLength - length of the string
	 * @return the generated random string
	 */
	public static String[] randomStringCustom (String[] pVowels, String[] pConsonants, int pLength) {
		String[] randomStr = new String[pLength];
		Random rand = new Random();
		
		for (int i = 0; i < pLength; i++) {
			String[] letters;
			if (rand.nextInt(10) > 2) {
				letters = pConsonants;
			} else {
				letters = pVowels;
			}
				
			randomStr[i] = letters[rand.nextInt(letters.length)];
		}
		
		return randomStr;
	}
}
