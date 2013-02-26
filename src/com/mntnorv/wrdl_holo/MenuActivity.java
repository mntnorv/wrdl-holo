package com.mntnorv.wrdl_holo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.util.StringGenerator;

public class MenuActivity extends Activity {

	public final static String GAME_STATE = "com.mntnorv.wrdl-holo.GAME_STATE";
	
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
}
