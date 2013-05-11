package com.mntnorv.wrdl_holo.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GameStatesTable {
	// Database table
	public static final String TABLE_STATES = "states";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LETTERS = "letters";
	public static final String COLUMN_SIZE = "size";
	public static final String COLUMN_GAMEMODE = "gamemode";
	public static final String COLUMN_GUESSED = "guessed";
	public static final String COLUMN_WORDS = "words";
	public static final String COLUMN_GUESSED_DATA = "guessed_data";
	public static final String COLUMN_POINTS = "points";
	
	public static final String[] ALL_COLUMNS = new String[] {
		COLUMN_ID, COLUMN_LETTERS, COLUMN_SIZE, COLUMN_GAMEMODE,
		COLUMN_GUESSED, COLUMN_WORDS, COLUMN_GUESSED_DATA, COLUMN_POINTS
	};

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_STATES
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_LETTERS + " text not null, " 
			+ COLUMN_SIZE + " integer not null, "
			+ COLUMN_GAMEMODE + " integer not null, "
			+ COLUMN_GUESSED + " integer not null, "
			+ COLUMN_WORDS + " integer not null, "
			+ COLUMN_GUESSED_DATA + " blob not null, "
			+ COLUMN_POINTS
			+ " integer not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(GameStatesTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_STATES);
		onCreate(database);
	}
}
