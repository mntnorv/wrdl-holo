package com.mntnorv.wrdl_holo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

public class GameActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        TableLayout tileTable = (TableLayout)findViewById(R.id.tileTable);
        
        for (int i = 0; i < 4; i++) {
        	TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(
            		LayoutParams.MATCH_PARENT,
            		LayoutParams.WRAP_CONTENT));
            
        	for (int j = 0; j < 4; j++) {
        		TileView tile = new TileView(this);
        		tile.setSize(getResources().getDisplayMetrics().widthPixels/4);
        		tile.setText("Qu");
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
