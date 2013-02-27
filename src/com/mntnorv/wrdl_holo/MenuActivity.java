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

	public final static String GAME_STATE = "com.mntnorv.wrdl_holo.GAME_STATE";
	
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
			Log.e("dictionary", "Error laoding dictionary from \"dict.hex\"");
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
        getContentResolver().insert(uri, values);
        
        startGameWithState(gameList.size() - 1);
	}
	
	private void startGameWithState(int stateId) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(GAME_STATE, gameList.get(stateId));
		startActivity(intent);
	}
	
	private OnItemClickListener mainMenuListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			startGameWithState(position);
		}
	};

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { GameStatesTable.COLUMN_ID, GameStatesTable.COLUMN_LETTERS, GameStatesTable.COLUMN_SIZE };
	    CursorLoader cursorLoader = new CursorLoader(this,
	        WrdlContentProvider.GAME_STATES_URI, projection, null, null, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		data.moveToFirst();
		while (!data.isAfterLast()) {
			String letterStr = data.getString(1);
			int size = data.getInt(2);
			
			String letters[] = GameState.stringToLetterArray(letterStr);
			
			GameState state = new GameState(size, letters);
			gameList.add(state);
			
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
