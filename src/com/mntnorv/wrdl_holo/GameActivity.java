package com.mntnorv.wrdl_holo;

import com.mntnorv.wrdl_holo.views.TileGridView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

public class GameActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_game);
        
        final TileGridView grid = (TileGridView)findViewById(R.id.mainTileGrid);
        final EditText wordField = (EditText)findViewById(R.id.currentWordField);
        
        final String[] letters = {
        		"A", "B", "C", "D",
        		"E", "F", "G", "H",
        		"I", "J", "K", "L",
        		"M", "N", "O", "Qu"};
        
        grid.setLetters(letters);
        grid.setOnWordChangeListener(new TileGridView.OnWordChangeListener() {	
			@Override
			public void onWordChange(String word) {
				wordField.setText(word);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
}
