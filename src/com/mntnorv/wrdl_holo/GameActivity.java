package com.mntnorv.wrdl_holo;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
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
        
        final String[] letters = {
        		"A", "B", "C", "D",
        		"E", "F", "G", "H",
        		"I", "J", "K", "L",
        		"M", "N", "O", "P"};
        
        final float tileSize = getResources().getDisplayMetrics().widthPixels/4;
        final int tiles = 4;
        
        final ArrayList<Integer> selected = new ArrayList<Integer>();
        
        tileTable.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getPointerCount() == 1) {
					float x = event.getX();
					float y = event.getY();
					
					int xt = (int) (x / tileSize);
					int yt = (int) (y / tileSize);
					
					boolean inside = xt >= 0 && xt < tiles && yt >= 0 && yt < tiles;
					
					if (event.getAction() == MotionEvent.ACTION_DOWN && inside) {
						currentWord += letters[yt*4 + xt];
						selected.add(yt*4 + xt);
					} else if (event.getAction() == MotionEvent.ACTION_MOVE && inside && selected.size() > 0) {
						int lastXT = selected.get(0) % tiles;
						int lastYT = selected.get(0) / tiles;
						int prevXT = -1;
						int prevYT = -1;
						
						if (selected.size() > 1) {
							prevXT = selected.get(1) % tiles;
							prevYT = selected.get(1) / tiles;
						}
						
						int dx = Math.abs(lastXT - xt);
						int dy = Math.abs(lastYT - yt);
						
						if (!(dx == 0 && dy == 0) && (dx <= 1 && dy <= 1) &&
							(((!selected.contains((Object)(yt*4 + xt))) && (selected.size() < tiles*tiles))
							|| (xt == prevXT && yt == prevYT))) {
							
							float xTile = xt*tileSize + tileSize/2;
							float yTile = yt*tileSize + tileSize/2;
							
							float distance = (x - xTile)*(x - xTile) + (y - yTile)*(y - yTile);
							float maxDistance = (tileSize*0.9f)/2;
							
							if (distance < maxDistance * maxDistance) {
								if (!selected.contains((Object)(yt*4 + xt))) {
									currentWord += letters[yt*4 + xt];
									selected.add(0, yt*4 + xt);
								} else {
									currentWord = currentWord.substring(0, currentWord.length()-letters[yt*4+xt].length());
									selected.remove(0);
								}
							}
						}
					} else {
						currentWord = "";
						selected.clear();
					}
					
					currentWordField.setText(currentWord);
				}
				
				return true;
			}
        });
        
        for (int i = 0; i < 4; i++) {
        	TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(
            		LayoutParams.MATCH_PARENT,
            		LayoutParams.WRAP_CONTENT));
            
        	for (int j = 0; j < 4; j++) {
        		TileView tile = new TileView(this);
        		tile.setSize(getResources().getDisplayMetrics().widthPixels/4);
        		tile.setText(letters[i*4 + j]);
        		tile.setColor(0xFFAAAAAA);
        		tile.setTextColor(0xFF000000);
        		tile.setTextSize((int) (48*getResources().getDisplayMetrics().density));
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
