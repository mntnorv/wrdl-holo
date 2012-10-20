package com.mntnorv.wrdl_holo;

import java.util.ArrayList;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class GridSequenceTouchListener implements OnTouchListener {
	/* STATIC */
	public static final byte ELEMENT_ADDED = 0x01;
	public static final byte ELEMENT_REMOVED = 0x02;
	public static final byte SEQUENCE_CLEARED = 0x00;
	
	/* FIELDS */
	private float tileWidth;
	private float tileHeight;
	private float tileMinDim;
	private int rows;
	private int columns;
	
	private ArrayList<Integer> selected;
	
	/* CONSTRUCTOR */
	/**
	 * Makes a new GridSequenceTouchListener
	 * @param tileWidth - grid tile width
	 * @param tileHeight - grid tile height
	 * @param rows - number of rows in grid
	 * @param columns - number of columns in grid
	 */
	public GridSequenceTouchListener(float tileWidth, float tileHeight, int rows, int columns) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.rows = rows;
		this.columns = columns;
		
		this.tileMinDim = Math.min(tileWidth, tileHeight);
		
		selected = new ArrayList<Integer> ();
	}

	/* SEQUENCE CHANGED */
	/**
	 * Gets called when the sequence of selected tiles
	 * in the grid is changed.
	 * @param sequence - a list of Integers representing 
	 * the sequence of selected tiles. The Integer is calculated
	 * like this: {@code row * numberOfColumns + column}. The last tile
	 * in the sequence is always the first in this list.
	 * @param changeType - represents the change that was made to
	 * the sequence. Can be equal to {@link #ELEMENT_ADDED},
	 * {@link #ELEMENT_REMOVED} or {@link #SEQUENCE_CLEARED}.
	 * @param elemChanged - value of the element added or removed.
	 * {@code -1} if {@code changeType} == {@link #SEQUENCE_CLEARED}.
	 */
	protected abstract void sequenceChanged (ArrayList<Integer> sequence, byte changeType, int elemChanged);
	
	/* ON TOUCH */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getPointerCount() == 1) {
			boolean sequenceChanged = false;
			byte changeType = SEQUENCE_CLEARED;
			int elemChanged = -1;
			
			float x = event.getX();
			float y = event.getY();
			
			int xt = (int) (x / tileWidth);
			int yt = (int) (y / tileHeight);
			
			boolean inside = xt >= 0 && xt < columns && yt >= 0 && yt < rows;
			
			if (event.getAction() == MotionEvent.ACTION_DOWN && inside) {
				selected.add(yt*4 + xt);
				sequenceChanged = true;
				changeType = ELEMENT_ADDED;
				elemChanged = yt*4 + xt;
			} else if (event.getAction() == MotionEvent.ACTION_MOVE && inside && selected.size() > 0) {
				int lastXT = selected.get(0) % columns;
				int lastYT = selected.get(0) / rows;
				int prevXT = -1;
				int prevYT = -1;
				
				if (selected.size() > 1) {
					prevXT = selected.get(1) % columns;
					prevYT = selected.get(1) / rows;
				}
				
				int dx = Math.abs(lastXT - xt);
				int dy = Math.abs(lastYT - yt);
				
				if (!(dx == 0 && dy == 0) && (dx <= 1 && dy <= 1) &&
					(((!selected.contains((Object)(yt*4 + xt))) && (selected.size() < rows*columns))
					|| (xt == prevXT && yt == prevYT))) {
					
					float xTile = xt*tileWidth + tileWidth/2;
					float yTile = yt*tileHeight + tileHeight/2;
					
					float distance = (x - xTile)*(x - xTile) + (y - yTile)*(y - yTile);
					float maxDistance = (tileMinDim*0.9f)/2;
					
					if (distance < maxDistance * maxDistance) {
						if (!selected.contains((Object)(yt*4 + xt))) {
							selected.add(0, yt*4 + xt);
							changeType = ELEMENT_ADDED;
							elemChanged = yt*4 + xt;
						} else {
							selected.remove(0);
							changeType = ELEMENT_REMOVED;
							elemChanged = lastYT*4 + lastXT;
						}
						
						sequenceChanged = true;
					}
				}
			} else {
				selected.clear();
				sequenceChanged = true;
			}
			
			if (sequenceChanged) {
				sequenceChanged (selected, changeType, elemChanged);
			}
		}
		
		return true;
	}

}
