package com.mntnorv.wrdl_holo.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WordsTable {
	// Database table
	public static final String TABLE_WORDS = "words";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_GAMEID = "gameid";
	public static final String COLUMN_ISGUESSED = "isguessed";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_WORDS
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_WORD + " text not null, " 
			+ COLUMN_GAMEID + " integer not null," 
			+ COLUMN_ISGUESSED
			+ " integer not null" 
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(WordsTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
		onCreate(database);
	}
}
