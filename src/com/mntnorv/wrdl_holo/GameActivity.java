package com.mntnorv.wrdl_holo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

import com.mntnorv.wrdl_holo.dict.Dictionary;
import com.mntnorv.wrdl_holo.dict.LetterGrid;
import com.mntnorv.wrdl_holo.views.TileGridView;

public class GameActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_game);
        
        final List<String> words = new ArrayList<String>();
        
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
				wordField.setText(word.toUpperCase());
			}
		});
        
        Dictionary dict = null;
        
		try {
			dict = new Dictionary(getAssets().open("dict.hex"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        LetterGrid lGrid = new LetterGrid(letters, 4, 4);
        words.addAll(lGrid.getWordsInGrid(dict));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
}
