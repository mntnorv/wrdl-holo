package com.mntnorv.wrdl_holo.db;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;

import com.mntnorv.wrdl_holo.GameState;

public class GameStateSource {
	private static final String BUNDLE_STATE_ID = "stateId";
	
	private final int MIN_LOADER_ID = LoaderIdDistributor.getNextId();
	private final int MAX_LOADER_ID = MIN_LOADER_ID + LoaderIdDistributor.getMaxIdsPerLoader();
	
	Context context;
	LoaderManager loaderManager;
	ContentResolver contentResolver;
	
	SparseArray<OnLoadFinishedListener> listeners = new SparseArray<OnLoadFinishedListener>();
	int nextId = MIN_LOADER_ID;
	
	public GameStateSource(Context context, LoaderManager loaderManager, ContentResolver contentResolver) {
		this.context = context;
		this.loaderManager = loaderManager;
		this.contentResolver = contentResolver;
	}
	
	public void getAllStates(OnLoadFinishedListener listener) {
		listeners.append(nextId, listener);
		loaderManager.initLoader(nextId, null, callbacks);
		calculateNextId();
	}
	
	public void getStateById(int id, OnLoadFinishedListener listener) {
		Bundle args = new Bundle();
		args.putInt(BUNDLE_STATE_ID, id);
		
		listeners.append(nextId, listener);
		loaderManager.initLoader(nextId, args, callbacks);
		calculateNextId();
	}
	
	public void insertGameState(GameState state) {
		Uri insertUri = WrdlContentProvider.GAME_STATES_URI;
		
        ContentValues values = new ContentValues();
        values.put(GameStatesTable.COLUMN_SIZE, state.getSize());
        values.put(GameStatesTable.COLUMN_LETTERS, GameState.letterArrayToString(state.getLetterArray()));
        
        Uri newUri = contentResolver.insert(insertUri, values);
        state.setId(Integer.parseInt(newUri.getLastPathSegment()));
	}
	
	public void updateGameState(GameState state) {
		Uri updateUri = Uri.parse(WrdlContentProvider.GAME_STATES_URI + "/" + Integer.toString(state.getId()));
		contentResolver.update(updateUri, state.toContentValues(), null, null);
	}
	
	private LoaderManager.LoaderCallbacks<Cursor> callbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Uri uri = WrdlContentProvider.GAME_STATES_URI;
			if (args.containsKey(BUNDLE_STATE_ID)) {
				uri = Uri.parse(uri + "/" + args.getInt(BUNDLE_STATE_ID));
			}
			
			CursorLoader cursorLoader = new CursorLoader(context,
					uri, GameStatesTable.ALL_COLUMNS, null, null, null);
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			OnLoadFinishedListener listener = listeners.get(loader.getId());
			if (listener != null) {
				List<GameState> results = new ArrayList<GameState>();
				data.moveToFirst();
				while (!data.isAfterLast()) {
					results.add(GameState.createFromCursor(data));
					data.moveToNext();
				}
				data.close();
			
				listener.onLoadFinished(results);
				listeners.remove(loader.getId());
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};
	
	private void calculateNextId() {
		nextId++;
		
		if (nextId == MAX_LOADER_ID) {
			nextId = MIN_LOADER_ID;
		}
	}
	
	public interface OnLoadFinishedListener {
		public void onLoadFinished(List<GameState> result);
	}
}
