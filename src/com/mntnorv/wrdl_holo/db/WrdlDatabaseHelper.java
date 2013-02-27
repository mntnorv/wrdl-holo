package com.mntnorv.wrdl_holo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WrdlDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "gamestates.db";
	private static final int DATABASE_VERSION = 1;
	
	public WrdlDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		GameStatesTable.onCreate(db);
		WordsTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		GameStatesTable.onUpgrade(db, oldVersion, newVersion);
		WordsTable.onUpgrade(db, oldVersion, newVersion);
	}
}
