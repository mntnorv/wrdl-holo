package com.mntnorv.wrdl_holo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mntnorv.wrdl_holo.db.GameStatesTable;
import com.mntnorv.wrdl_holo.db.WrdlContentProvider;
import com.mntnorv.wrdl_holo.dict.DictionaryProvider;
import com.mntnorv.wrdl_holo.util.StringGenerator;

public class MenuActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	public final static String GAME_STATE_ID = "com.mntnorv.wrdl_holo.GAME_STATE_ID";
	
	MainMenuAdapter menuAdapter;
	private List<GameState> gameList = new ArrayList<GameState>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		DictionaryProvider.loadDictionary(this);
		
		ListView menuListView = (ListView)findViewById(R.id.menu_list_view);
		menuAdapter = new MainMenuAdapter(this, gameList);
		menuListView.setAdapter(menuAdapter);
		menuListView.setOnItemClickListener(mainMenuListener);
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	public void startNewGame(View view) {
		GameState newGame = new GameState(4, StringGenerator.randomString(4 * 4), GameModes.INFINITY);
        gameList.add(newGame);
        
        menuAdapter.notifyDataSetChanged();
        
        Uri uri = WrdlContentProvider.GAME_STATES_URI;
        ContentValues gameStateValues = newGame.toContentValues();
        Uri newUri = getContentResolver().insert(uri, gameStateValues);
        newGame.setId(Integer.parseInt(newUri.getLastPathSegment()));
        
        startGameWithStateId(newGame.getId());
	}
	
	private void startGameWithStateId(int stateId) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(GAME_STATE_ID, stateId);
		startActivity(intent);
	}
	
	private OnItemClickListener mainMenuListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			startGameWithStateId(gameList.get(position).getId());
		}
	};

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(this,
	        WrdlContentProvider.GAME_STATES_URI, GameStatesTable.ALL_COLUMNS,
	        null, null, null);
		
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		data.moveToFirst();
		while (!data.isAfterLast()) {
			gameList.add(GameState.createFromCursor(data));
			data.moveToNext();
		}
		data.close();
		menuAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		gameList.clear();
		menuAdapter.notifyDataSetChanged();
	}
}
