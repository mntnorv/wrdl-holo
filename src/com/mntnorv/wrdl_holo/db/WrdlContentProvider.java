package com.mntnorv.wrdl_holo.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class WrdlContentProvider extends ContentProvider {

	// Database
	private WrdlDatabaseHelper database;

	// Used for the UriMacher
	private static final int GAME_STATES = 10;
	private static final int GAME_STATE_ID = 20;

	private static final String AUTHORITY = "com.mntnorv.wrdl_holo.db.WrdlContentProvider";

	// Paths
	private static final String GAME_STATES_BASE_PATH = "gamestates";
	public static final Uri GAME_STATES_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + GAME_STATES_BASE_PATH);

	// MIME types
	public static final String STATES_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/vnd.mntnorv.wrdl_holo.gamestate";
	public static final String STATES_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/vnd.mntnorv.wrdl_holo.gamestate";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, GAME_STATES_BASE_PATH, GAME_STATES);
		sURIMatcher.addURI(AUTHORITY, GAME_STATES_BASE_PATH + "/#", GAME_STATE_ID);
	}

	@Override
	public boolean onCreate() {
		database = new WrdlDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (sURIMatcher.match(uri)) {
		case GAME_STATES:
			queryBuilder.setTables(GameStatesTable.TABLE_STATES);
			break;
		case GAME_STATE_ID:
			queryBuilder.setTables(GameStatesTable.TABLE_STATES);
			queryBuilder.appendWhere(GameStatesTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case GAME_STATES:
			return STATES_DIR_TYPE;
		case GAME_STATE_ID:
			return STATES_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = database.getWritableDatabase();
		String basePath = "";
		long insertId = 0;
		
		switch (sURIMatcher.match(uri)) {
		case GAME_STATES:
			insertId = db.insert(GameStatesTable.TABLE_STATES, null, values);
			basePath = GAME_STATES_BASE_PATH;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return Uri.parse(basePath + "/" + insertId);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsDeleted = 0;
		
		switch (sURIMatcher.match(uri)) {
		case GAME_STATES:
			rowsDeleted = db.delete(GameStatesTable.TABLE_STATES,
					selection, selectionArgs);
			break;
		case GAME_STATE_ID:
			String gameId = uri.getLastPathSegment();
			if (!TextUtils.isEmpty(selection)) {
				selection = GameStatesTable.COLUMN_ID + "=" + gameId
						+ " and " + selection;
			}
			rowsDeleted = db.delete(GameStatesTable.TABLE_STATES,
					selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsUpdated = 0;
		
		switch (sURIMatcher.match(uri)) {
		case GAME_STATES:
			rowsUpdated = db.update(GameStatesTable.TABLE_STATES,
					values, selection, selectionArgs);
			break;
		case GAME_STATE_ID:
			String gameId = uri.getLastPathSegment();
			if (!TextUtils.isEmpty(selection)) {
				selection = GameStatesTable.COLUMN_ID + "=" + gameId
						+ " and " + selection;
			}
			rowsUpdated = db.update(GameStatesTable.TABLE_STATES,
					values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return rowsUpdated;
	}
}
