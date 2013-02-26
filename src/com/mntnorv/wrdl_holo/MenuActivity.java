package com.mntnorv.wrdl_holo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.util.StringGenerator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class MenuActivity extends Activity {

	public final static String GAME_STATE = "com.mntnorv.wrdl-holo.GAME_STATE";
	
	private GameState testGameState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		// Load dictionary
        Dictionary dict = null;
		try {
			dict = new Dictionary(getAssets().open("dict.hex"));
		} catch (IOException e) {
			Log.e("dictionary", "Error laoding dictionary from \"dict.hex\"");
		}
        
        // Create a game
        testGameState = new GameState(4, StringGenerator.randomString(4 * 4));
        testGameState.findAllWords(dict);
        List<GameState> states = new ArrayList<GameState>();
        states.add(testGameState);
		
		ListView menuListView = (ListView)findViewById(R.id.menuListView);
		MainMenuAdapter menuAdapter = new MainMenuAdapter(this, states);
		menuListView.setAdapter(menuAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	public void startNewGame(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(GAME_STATE, testGameState);
		startActivity(intent);
	}
}
