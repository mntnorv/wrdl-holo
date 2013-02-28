package com.mntnorv.wrdl_holo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.mntnorv.wrdl_holo.db.GameStatesTable;
import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.dict.LetterGrid;

public class GameState implements Parcelable {
	private int id = -1;
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
	
	public GameState(int id, int size, String[] grid) {
		this (size, grid);
		
		this.id = id;
	}
	
	private GameState(Parcel in) {
		allWords = new ArrayList<String>();
		guessedWords = new ArrayList<String>();
		
		size = in.readInt();
		letterArray = new String[size*size];
		in.readStringArray(letterArray);
		in.readStringList(allWords);
		in.readStringList(guessedWords);
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
	
	/* SETTERS */
	public void setId(int id) {
		this.id = id;
	}

	/* GETTERS */
	public int getId() {
		return id;
	}
	
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(size);
		out.writeStringArray(letterArray);
		out.writeStringList(allWords);
		out.writeStringList(guessedWords);
	}
	
	public static final Parcelable.Creator<GameState> CREATOR = new Parcelable.Creator<GameState>() {
        public GameState createFromParcel(Parcel in) {
            return new GameState(in);
        }

        public GameState[] newArray(int size) {
            return new GameState[size];
        }
    };
    
    public static GameState createFromCursor(Cursor cursor) {
		int sizeIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_SIZE);
		int lettersIndex = cursor.getColumnIndexOrThrow(GameStatesTable.COLUMN_LETTERS);
		String letterStr = cursor.getString(lettersIndex);
		int size = cursor.getInt(sizeIndex);
		
		String letters[] = GameState.stringToLetterArray(letterStr);
		
		return new GameState(size, letters);
	}
    
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
