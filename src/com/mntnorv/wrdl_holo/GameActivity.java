package com.mntnorv.wrdl_holo;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

public class GameActivity extends Activity {

	private String currentWord = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        final TableLayout tileTable = (TableLayout)findViewById(R.id.tileTable);
        final EditText currentWordField = (EditText)findViewById(R.id.currentWordField);
        final RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        
        final String[] letters = {
        		"A", "B", "C", "D",
        		"E", "F", "G", "H",
        		"I", "J", "K", "L",
        		"M", "N", "O", "Qu"};
        
        final float tileSize = getResources().getDisplayMetrics().widthPixels/5;
        final int tiles = 4;
        
        final GridIndicatorView test = new GridIndicatorView(this, tileSize, tileSize, tiles, tiles);
        test.addIndicator(1, 1, 1, 2);
        test.addIndicator(1, 1, 2, 2);
        test.addIndicator(1, 1, 2, 1);
        test.addIndicator(1, 1, 0, 2);
        test.addIndicator(1, 1, 0, 1);
        test.addIndicator(1, 1, 0, 0);
        test.addIndicator(1, 1, 1, 0);
        test.addIndicator(1, 1, 2, 0);
        mainLayout.addView(test);
        
        tileTable.setOnTouchListener(new GridSequenceTouchListener(tileSize, tileSize, tiles, tiles) {
			@Override
			protected void sequenceChanged(ArrayList<Integer> sequence, byte changeType, int elemChanged) {
				if (changeType == GridSequenceTouchListener.ELEMENT_ADDED) {
					currentWord += letters[elemChanged];
				} else if (changeType == GridSequenceTouchListener.ELEMENT_REMOVED) {
					currentWord = currentWord.substring(0, currentWord.length() - letters[elemChanged].length());
				} else {
					currentWord = "";
				}
				
				currentWordField.setText(currentWord);
			}
        });
        
        for (int i = 0; i < 4; i++) {
        	TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(
            		LayoutParams.MATCH_PARENT,
            		LayoutParams.WRAP_CONTENT));
            
        	for (int j = 0; j < 4; j++) {
        		TileView tile = new TileView(this);
        		tile.setSize(getResources().getDisplayMetrics().widthPixels/5);
        		tile.setText(letters[i*4 + j]);
        		tile.setColor(0xFFAAAAAA);
        		tile.setTextColor(0xFF000000);
        		tile.setTextSize((int) (36*getResources().getDisplayMetrics().density));
        		row.addView(tile);
        	}
        	
        	tileTable.addView(row, new TableLayout.LayoutParams(
        			LayoutParams.MATCH_PARENT,
        			LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
}
