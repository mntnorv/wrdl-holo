package com.mntnorv.wrdl_holo.views;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mntnorv.wrdl_holo.GridSequenceTouchListener;
import com.mntnorv.wrdl_holo.R;

public class TileGridView extends RelativeLayout {
	/* FIELDS */
	private TileView[] tiles;
	private float width;
	private float height;
	private int columns;
	private int rows;
	private boolean touch;
	
	private GridIndicatorView indicators;
	
	private String[] letters;
	private int tileColor;
	private int tileHighlightColor;
	private int tileTextColor;
	private int indicatorColor;
	private float indicatorHeight;
	
	private String currentWord;
	
	private OnWordChangeListener wordChangeListener;
	private OnWordSelectedListener wordSelectedListener;
	
	/* CONSTRUCTORS */
	public TileGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGridView();

		TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TileGridView);
		
		//int w = a.getDimensionPixelSize(R.styleable.TileGridView_tileGridViewWidth, 0);
		/*int w = a.getLayoutDimension(R.styleable.TileGridView_android_layout_width, 0);
		if (w != 0) {
			width = w;
		}*/
		
		//int h = a.getDimensionPixelSize(R.styleable.TileGridView_tileGridViewHeight, 0);
		/*int h = a.getLayoutDimension(R.styleable.TileGridView_android_layout_height, 0);
		if (h != 0) {
			height = h;
		}*/
		
		int c = a.getInt(R.styleable.TileGridView_columns, 0);
		if (c > 0) {
			columns = c;
		}
		
		int r = a.getInt(R.styleable.TileGridView_rows, 0);
		if (r > 0) {
			rows = r;
		}
		
		tileColor = a.getColor(R.styleable.TileGridView_tileColor, 0xFFAAAAAA);
		tileHighlightColor = a.getColor(R.styleable.TileGridView_tileHighlightColor, 0xFFAAAAAA);
		tileTextColor = a.getColor(R.styleable.TileGridView_tileTextColor, 0xFF000000);
		indicatorColor = a.getColor(R.styleable.TileGridView_indicatorColor, 0xBB63BAF9);
		
		int ih = a.getDimensionPixelSize(R.styleable.TileGridView_indicatorHeight, 0);
		if (ih != 0) {
			indicatorHeight = ih;
		}
		
		touch = a.getBoolean(R.styleable.TileGridView_respondToTouch, false);
		
		if (isInEditMode()) {
			TextView label = new TextView(context);
			label.setWidth((int) width);
			label.setHeight((int) height);
			label.setText("TileGridView");
			label.setGravity(Gravity.CENTER);
			this.addView(label);
		} else {
			createGridView(context);
		}
	}
	
	/* INIT */
	private void initGridView() {
		tileColor = 0xFFAAAAAA;
		tileHighlightColor = 0xFFAAAAAA;
		tileTextColor = 0xFF000000;
		indicatorColor = 0xBB63BAF9;
		indicatorHeight = 8 * getResources().getDisplayMetrics().density;
		
		width = 100;
		height = 100;
		columns = 1;
		rows = 1;
		
		currentWord = "";
		
		wordChangeListener = null;
		wordSelectedListener = null;
	}
	
	/* CREATE */
	private void createGridView(Context context) {
		if (width > 0 && height > 0 && columns > 0 && rows > 0) {
			FrameLayout frame = new FrameLayout(context);
			TableLayout tileTable = generateTileGrid(context);
			
			if (touch) {
				indicators = new GridIndicatorView(context,
						width/columns, height/rows, columns, rows,
						indicatorHeight, indicatorColor);
				addTouchListener();
				frame.addView(indicators);
			}
			
			frame.addView(tileTable);
			
			this.addView(frame);
		} else {
			throw new IllegalStateException("GridView must be initialized with a width, height, number of columns and rows");
		}
	}
	
	/* ADD TOUCH LISTENER */
	private void addTouchListener() {
		this.setOnTouchListener(new GridSequenceTouchListener(width/columns, height/rows, columns, rows) {
			@Override
			protected void sequenceChanged(ArrayList<Integer> sequence, byte changeType, int elemChanged) {
				if (changeType == GridSequenceTouchListener.ELEMENT_ADDED) {
					currentWord += letters[elemChanged];
					
					if (sequence.size() > 1) {
						indicators.addIndicator(sequence.get(1)%columns, sequence.get(1)/rows, elemChanged%columns, elemChanged/rows);
					}
					
					tiles[elemChanged].setHighlighted(true);
				} else if (changeType == GridSequenceTouchListener.ELEMENT_REMOVED) {
					currentWord = currentWord.substring(0, currentWord.length() - letters[elemChanged].length());
					indicators.removeLastIndicator();
					tiles[elemChanged].setHighlighted(false);
				} else {
					if (wordSelectedListener != null) {
						wordSelectedListener.onWordSelected(currentWord);
					}
					
					currentWord = "";
					indicators.clearIndicators();
					
					for (TileView tile: tiles) {
						tile.setHighlighted(false);
					}
				}
				
				if (wordChangeListener != null) {
					wordChangeListener.onWordChange(currentWord);
				}
			}
        });
	}
	
	/* GENERATE A TILE GRID */
	private TableLayout generateTileGrid(Context context) {
		tiles = new TileView[rows*columns];
		letters = new String[rows*columns];
		TableLayout tileTableLayout = new TableLayout(context);
		
		for (int i = 0; i < rows; i++) {
        	TableRow row = new TableRow(context);
            row.setLayoutParams(new TableRow.LayoutParams(
            		LayoutParams.MATCH_PARENT,
            		LayoutParams.WRAP_CONTENT));
            
        	for (int j = 0; j < columns; j++) {
        		TileView tile = new TileView(context);
        		tile.setWidth(width/columns);
        		tile.setHeight(height/rows);
        		tile.setText("A");
        		tile.setColor(tileColor);
        		tile.setHighlightColor(tileHighlightColor);
        		tile.setTextColor(tileTextColor);
        		tile.setTextSize((int) (36*getResources().getDisplayMetrics().density));
        		row.addView(tile);
        		
        		tiles[i*columns+j] = tile;
        		letters[i*columns+j] = "A";
        	}
        	
        	tileTableLayout.addView(row, new TableLayout.LayoutParams(
        			LayoutParams.MATCH_PARENT,
        			LayoutParams.WRAP_CONTENT));
        }
		
		return tileTableLayout;
	}
	
	/* SETTERS */
	/**
	 * @param letters - an array of strings, each element corresponding to a tile.
	 * The letter of a tile in the {@code m}-th column and the {@code n}-th row is
	 * element number {@code n*numberOfColumns + m}. Size MUST be equal or bigger
	 * than {@code columns*rows}.
	 */
	public void setLetters(String[] letters) {
		if (letters.length >= columns*rows) {
			for (int i = 0; i < columns*rows; i++) {
				tiles[i].setText(letters[i]);
				this.letters[i] = letters[i];
			}
		} else {
			throw new IllegalArgumentException("Size of letter array MUST be equal or bigger than columns*rows");
		}
	}
	
	public void setWidth(float width) {
		this.width = width;
		if (!isInEditMode() && tiles != null) {
			for (TileView tile: tiles) {
				tile.setWidth(width/columns);
			}
		}
	}
	
	public void setHeight(float height) {
		this.height = height;
		if (!isInEditMode() && tiles != null) {
			for (TileView tile: tiles) {
				tile.setHeight(height/rows);
			}
		}
	}
	
	public void setTileBackground(int color) {
		tileColor = color;
		for (TileView tile: tiles) {
			tile.setColor(color);
		}
	}
	
	public void setTileHighlighColor(int color) {
		tileHighlightColor = color;
		for (TileView tile: tiles) {
			tile.setHighlightColor(color);
		}
	}
	
	public void setTileTextColor(int color) {
		tileTextColor = color;
		for (TileView tile: tiles) {
			tile.setTextColor(color);
		}
	}
	
	public void setOnWordChangeListener(OnWordChangeListener listener) {
		wordChangeListener = listener;
	}
	
	public void setOnWordSelectedListener(OnWordSelectedListener listener) {
		wordSelectedListener = listener;
	}
	
	/* INTERFACES */
	public interface OnWordChangeListener {
		public abstract void onWordChange(String word);
	}
	
	public interface OnWordSelectedListener {
		public abstract void onWordSelected(String word);
	}
	
	/* MEASURE */
	@Override
	protected void onMeasure (int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
		
		setWidth(super.getMeasuredWidth());
		setHeight(super.getMeasuredHeight());
	}
}
