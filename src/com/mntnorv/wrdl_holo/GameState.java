package com.mntnorv.wrdl_holo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.mntnorv.wrdl_holo.db.GameStatesTable;
import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.dict.LetterGrid;
import com.mntnorv.wrdl_holo.dict.ScoreCounter;
import com.mntnorv.wrdl_holo.util.WrdlScoreCounter;

public class GameState {
	private int id = -1;
	private int size;
	private int gamemode;
	private String[] letterArray;
	private byte[] guessed;

	private List<String> allWords;
	private List<String> guessedWords;
	private ScoreCounter scoreCounter;

	private int wordCountCache = 0;
	private int guessedCountCache = 0;

	//================================================================================
	// Constructors
	//================================================================================

	public GameState(int size, String[] grid, int gamemode) {
		allWords = new ArrayList<String>();
		guessedWords = new ArrayList<String>();
		scoreCounter = new WrdlScoreCounter();

		this.size = size;
		this.gamemode = gamemode;

		// Copy letter array
		letterArray = grid.clone();
	}

	private GameState(int id, int size, String[] grid, int gamemode,
			int wordCount, int guessedCount, byte[] guessed, int points) {
		this (size, grid, gamemode);
		this.id = id;
		this.guessed = guessed.clone();
		this.scoreCounter.setTotalScore(points);
		this.wordCountCache = wordCount;
		this.guessedCountCache = guessedCount;
	}

	//================================================================================
	// General public methods
	//================================================================================
	public void findAllWords(Dictionary dict) {
		LetterGrid lGrid = new LetterGrid(letterArray, size, size);
		allWords.addAll(lGrid.getWordsInGrid(dict));

		boolean createGuessedArray = false;
		if (guessed == null) {
			createGuessedArray = true;
		} else if (guessed.length == 1 && guessed[0] == 0x00) {
			createGuessedArray = true;
		}

		if (createGuessedArray) {
			guessed = new byte[allWords.size()/8 + 1];
			for (int i = 0; i < guessed.length; i++) {
				guessed[i] = 0x00;
			}
		}
	}

	public void findGuessedWords() {
		byte mask = 1;

		for (int i = 0; i < guessed.length; i++) {
			byte currentByte = guessed[i];

			for (int j = 0; j < 8; j++) {
				if (i*8 + j == allWords.size()) {
					break;
				}

				if ((currentByte & mask) == 1) {
					guessedWords.add(allWords.get(i*8 + j));
				}

				currentByte = (byte) (currentByte >> 1);
			}
		}
	}

	public void addGuessedWord(String word) {
		guessedWords.add(word);

		int wordIndex = Collections.binarySearch(allWords, word);
		int byteIndex = wordIndex / 8;
		int byteMask = 1 << (wordIndex % 8);
		guessed[byteIndex] = (byte) (guessed[byteIndex] | byteMask);

		scoreCounter.addWordScore(word);
	}

	public boolean isGuessed(String word) {
		return guessedWords.contains(word);
	}

	public boolean isWordInGrid(String word) {
		return Collections.binarySearch(allWords, word) >= 0;
	}

	//================================================================================
	// Setters
	//================================================================================

	public void setId(int id) {
		this.id = id;
	}

	//================================================================================
	// Getters
	//================================================================================

	public int getId() {
		return id;
	}

	public int getSize() {
		return size;
	}

	public int getGamemode() {
		return gamemode;
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
		if (allWords.size() != 0) {
			return allWords.size();
		} else {
			return wordCountCache;
		}
	}

	public int getGuessedWordCount() {
		if (guessedWords.size() != 0) {
			return guessedWords.size();
		} else {
			return guessedCountCache;
		}
	}

	public int getScore() {
		return scoreCounter.getTotalScore();
	}

	public ScoreCounter getScoreCounter() {
		return scoreCounter;
	}

	//================================================================================
	// Database methods
	//================================================================================

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(GameStatesTable.COLUMN_SIZE, getSize());
		values.put(GameStatesTable.COLUMN_LETTERS, GameState.letterArrayToString(getLetterArray()));
		values.put(GameStatesTable.COLUMN_GAMEMODE, gamemode);
		values.put(GameStatesTable.COLUMN_WORDS, getWordCount());
		values.put(GameStatesTable.COLUMN_GUESSED, getGuessedWordCount());
		values.put(GameStatesTable.COLUMN_POINTS, scoreCounter.getTotalScore());

		if (guessed != null) {
			values.put(GameStatesTable.COLUMN_GUESSED_DATA, guessed);
		} else {
			values.put(GameStatesTable.COLUMN_GUESSED_DATA, new byte[] {0x00});
		}

		return values;
	}

	public static GameState createFromCursor(Cursor cursor) {
		int idIndex = cursor.getColumnIndex(GameStatesTable.COLUMN_ID);
		int sizeIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_SIZE);
		int lettersIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_LETTERS);
		int gamemodeIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_GAMEMODE);
		int wordCountIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_WORDS);
		int guessedCountIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_GUESSED);
		int guessedDataIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_GUESSED_DATA);
		int pointsIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_POINTS);

		int id = cursor.getInt(idIndex);
		int size = cursor.getInt(sizeIndex);
		String letterStr = cursor.getString(lettersIndex);
		int gamemode = cursor.getInt(gamemodeIndex);
		int wordCount = cursor.getInt(wordCountIndex);
		int guessedCount = cursor.getInt(guessedCountIndex);
		byte[] guessed = cursor.getBlob(guessedDataIndex);
		int points = cursor.getInt(pointsIndex);

		String letters[] = GameState.stringToLetterArray(letterStr);

		GameState state = new GameState(id, size, letters, gamemode,
				wordCount, guessedCount, guessed, points);

		return state;
	}

	//================================================================================
	// Static methods
	//================================================================================

	public static String letterArrayToString(String[] letters) {
		String letterStr = "";

		for (String letter: letters) {
			letterStr += letter;
		}

		return letterStr;
	}

	public static String[] stringToLetterArray(String letterStr) {
		List<String> letterList = new ArrayList<String>();

		int letter = -1;
		for (int j = 0; j < letterStr.length(); j++) {
			if (Character.isUpperCase(letterStr.charAt(j))) {
				letter++;
				letterList.add("" + letterStr.charAt(j));
			} else {
				letterList.set(letter, letterList.get(letter) + letterStr.charAt(j));
			}
		}

		String[] letterArray = new String[letterList.size()];
		letterList.toArray(letterArray);

		return letterArray;
	}
}
