package com.mntnorv.wrdl_holo;

import java.io.IOException;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mntnorv.wrdl_holo.db.GameStatesTable;
import com.mntnorv.wrdl_holo.db.WrdlContentProvider;
import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.util.StringGenerator;

public class MenuActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	public final static String GAME_STATE_URI = "com.mntnorv.wrdl_holo.GAME_STATE_URI";
	
	MainMenuAdapter menuAdapter;
	
	private Dictionary dict;
	private List<GameState> gameList = new ArrayList<GameState>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		// Load dictionary
		try {
			dict = new Dictionary(getAssets().open("dict.hex"));
		} catch (IOException e) {
			Log.e("dictionary", "Error loading dictionary from \"dict.hex\"");
		}
		
		ListView menuListView = (ListView)findViewById(R.id.menuListView);
		menuAdapter = new MainMenuAdapter(this, gameList);
		menuListView.setAdapter(menuAdapter);
		menuListView.setOnItemClickListener(mainMenuListener);
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	public void startNewGame(View view) {
		GameState newGame = new GameState(4, StringGenerator.randomString(4 * 4));
        newGame.findAllWords(dict);
        gameList.add(newGame);
        
        menuAdapter.notifyDataSetChanged();
        
        Uri uri = WrdlContentProvider.GAME_STATES_URI;
        ContentValues values = new ContentValues();
        values.put(GameStatesTable.COLUMN_SIZE, newGame.getSize());
        values.put(GameStatesTable.COLUMN_LETTERS, GameState.letterArrayToString(newGame.getLetterArray()));
        Uri newUri = getContentResolver().insert(uri, values);
        Log.d("uri", newUri.toString());
        newGame.setId(Integer.parseInt(newUri.getLastPathSegment()));
        
        startGameWithStateId(newGame.getId());
	}
	
	private void startGameWithStateId(int stateId) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(GAME_STATE_URI, WrdlContentProvider.GAME_STATES_URI + "/" + stateId);
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
		String[] projection = { GameStatesTable.COLUMN_ID, GameStatesTable.COLUMN_LETTERS, GameStatesTable.COLUMN_SIZE };
	    CursorLoader cursorLoader = new CursorLoader(this,
	        WrdlContentProvider.GAME_STATES_URI, projection, null, null, GameStatesTable.COLUMN_ID);
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
