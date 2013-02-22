package com.mntnorv.wrdl_holo.views;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mntnorv.wrdl_holo.GridSequenceTouchListener;
import com.mntnorv.wrdl_holo.R;
import com.tomgibara.android.util.SquareGridLayout;

public class TileGridView extends FrameLayout {
	//================================================================================
	// Fields
	//================================================================================
	private Context context;
	
	/* LAYOUT */
	private int size;
	private float width;
	private float height;
	
	/* STATE */
	private boolean touch;
	private String currentWord;
	private String[] letters;
	
	/* VIEWS */
	private TileView[] tiles;
	private GridIndicatorView indicators;
	private ViewGroup tileViewGroup;
	
	/* APPEARANCE */
	private int tileTextColor;
	private int indicatorColor;
	private int backgroundColor;
	private float indicatorHeight;
	
	private boolean enableIndicatorShadow;
	private int indicatorShadowColor;
	private int indicatorShadowXOffset;
	private int indicatorShadowYOffset;
	
	/* LISTENERS */
	private GridSequenceTouchListener touchListener;
	private OnWordChangeListener wordChangeListener;
	private OnWordSelectedListener wordSelectedListener;
	
	//================================================================================
	// Constructors
	//================================================================================
	public TileGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGridView();

		this.context = context;
		
		TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TileGridView);
		
		tileTextColor = a.getColor(R.styleable.TileGridView_tileTextColor, 0xFF000000);
		indicatorColor = a.getColor(R.styleable.TileGridView_indicatorColor, 0xFF000000);
		backgroundColor = a.getColor(R.styleable.TileGridView_backgroundColor, 0x00000000);
		
		int ih = a.getDimensionPixelSize(R.styleable.TileGridView_indicatorHeight, 0);
		if (ih != 0) {
			indicatorHeight = ih;
		}
		
		touch = a.getBoolean(R.styleable.TileGridView_respondToTouch, false);
		
		enableIndicatorShadow = a.getBoolean(R.styleable.TileGridView_indicatorEnableShadow, false);
		indicatorShadowColor = a.getColor(R.styleable.TileGridView_indicatorShadowColor, 0xFF000000);
		indicatorShadowXOffset = a.getDimensionPixelSize(R.styleable.TileGridView_indicatorShadowXOffset, 5);
		indicatorShadowYOffset = a.getDimensionPixelSize(R.styleable.TileGridView_indicatorShadowYOffset, 5);
		
		this.setBackgroundColor(backgroundColor);
		
		if (isInEditMode()) {
			this.addView(generateTileGrid(context, size));
		}
		
		a.recycle();
	}
	
	//================================================================================
	// Init, create methods
	//================================================================================
	
	/**
	 * Sets fields to their default values
	 */
	private void initGridView() {
		tileTextColor = 0xFF000000;
		indicatorColor = 0xFF000000;
		indicatorHeight = 8 * getResources().getDisplayMetrics().density;
		
		width = 100;
		height = 100;
		size = 1;
		
		currentWord = "";
		
		wordChangeListener = null;
		wordSelectedListener = null;
	}
	
	/**
	 * Initializes TileGridView with the specified number of columns and rows
	 * @param size - number of columns and rows
	 */
	public void create(int size) {
		this.size = size;
		
		createGridView();
	}
	
	/**
	 * Initializes TileGridView
	 */
	private void createGridView() {
		if (width > 0 && height > 0 && size > 0) {
			tileViewGroup = generateTileGrid(context, size);
			
			if (touch) {
				addIndicators();
				addTouchListener();
			}
			
			this.addView(tileViewGroup);
		} else {
			throw new IllegalStateException("GridView must be initialized with a width, height, number of columns and rows");
		}
	}
	
	/**
	 * Adds indicators to this view
	 */
	private void addIndicators() {
		indicators = new GridIndicatorView(context,
				width/size, height/size, size, size,
				indicatorHeight, indicatorColor);
		indicators.setShadowColor(indicatorShadowColor);
		indicators.setShadowOffset(indicatorShadowXOffset, indicatorShadowYOffset);
		
		if (enableIndicatorShadow) {
			indicators.enableShadow();
		}
		
		this.addView(indicators, 0);
	}
	
	//================================================================================
	// Private helper methods
	//================================================================================
	
	/**
	 * Adds a touch listener to this view. Used for word selection.
	 */
	private void addTouchListener() {
		touchListener = new GridSequenceTouchListener(width/size, height/size, size, size) {
			@Override
			protected void sequenceChanged(ArrayList<Integer> sequence, byte changeType, int elemChanged) {
				if (changeType == GridSequenceTouchListener.ELEMENT_ADDED) {
					currentWord += letters[elemChanged].toUpperCase(Locale.US);
					
					indicators.addHighlightedTile(elemChanged%size, elemChanged/size);
				} else if (changeType == GridSequenceTouchListener.ELEMENT_REMOVED) {
					currentWord = currentWord.substring(0, currentWord.length() - letters[elemChanged].length());
					indicators.removeLastHighlight();
				} else {
					if (wordSelectedListener != null) {
						wordSelectedListener.onWordSelected(currentWord);
					}
					
					currentWord = "";
					indicators.clearHighlights();
				}
				
				if (wordChangeListener != null) {
					wordChangeListener.onWordChange(currentWord);
				}
			}
        };
        
        this.setOnTouchListener(touchListener);
	}
	
	/**
	 * Generates a grid of tiles
	 * @param context - current context
	 * @param size - number of rows/columns
	 * @return a ViewGroup containing the tiles
	 */
	private ViewGroup generateTileGrid(Context context, int size) {
		tiles = new TileView[size*size];
		letters = new String[size*size];
		SquareGridLayout layout = new SquareGridLayout(context);
		layout.setSize(size);
		
		for (int i = 0; i < size; i++) {
        	for (int j = 0; j < size; j++) {
        		TileView tile = new TileView(context);
        		tile.setText("A");
        		tile.setTextColor(tileTextColor);
        		tile.setTextSize((int) (36*getResources().getDisplayMetrics().density));
        		tile.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        		layout.addView(tile);
        		
        		tiles[i*size+j] = tile;
        		letters[i*size+j] = "A";
        	}
        }
		
		return layout;
	}
	
	//================================================================================
	// Setters
	//================================================================================
	
	/**
	 * @param letters - an array of strings, each element corresponding to a tile.
	 * The letter of a tile in the {@code m}-th column and the {@code n}-th row is
	 * element number {@code n*numberOfColumns + m}. Size MUST be equal or bigger
	 * than {@code columns*rows}.
	 */
	public void setLetters(String[] letters) {
		if (letters.length >= size*size) {
			for (int i = 0; i < size*size; i++) {
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
			if (touch) {
				indicators.setWidth(width);
				touchListener.setWidth(width);
			}
			
			if (width < height) {
				for (TileView tile: tiles) {
					tile.setSize(width/size);
				}
			}
		}
	}
	
	public void setHeight(float height) {
		this.height = height;
		
		if (!isInEditMode() && tiles != null) {
			if (touch) {
				indicators.setHeight(height);
				touchListener.setHeight(height);
			}
			
			if (height < width) {
				for (TileView tile: tiles) {
					tile.setSize(height/size);
				}
			}
		}
	}
	
	public void setTileTextColor(int color) {
		tileTextColor = color;
		for (TileView tile: tiles) {
			tile.setTextColor(color);
		}
	}
	
	public void setIndicatorColor(int color) {
		indicatorColor = color;
		indicators.setIndicatorColor(color);
	}
	
	public void setOnWordChangeListener(OnWordChangeListener listener) {
		wordChangeListener = listener;
	}
	
	public void setOnWordSelectedListener(OnWordSelectedListener listener) {
		wordSelectedListener = listener;
	}
	
	public void enableTouch() {
		if (!touch) {
			addIndicators();
			addTouchListener();
		}
		
		touch = true;
	}
	
	public void disableTouch() {
		if (touch) {
			this.removeViewAt(0);
			this.setOnTouchListener(null);
			touchListener = null;
		}
		
		touch = false;
	}
	
	//================================================================================
	// Interfaces
	//================================================================================
	
	public interface OnWordChangeListener {
		public abstract void onWordChange(String word);
	}
	
	public interface OnWordSelectedListener {
		public abstract void onWordSelected(String word);
	}
	
	//================================================================================
	// Measure methods
	//================================================================================
	
	@Override
	public void onMeasure (int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
		
		this.setWidth(getMeasuredWidth());
		this.setHeight(getMeasuredHeight());
	}
}
