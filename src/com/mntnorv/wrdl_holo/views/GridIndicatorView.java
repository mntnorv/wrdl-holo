package com.mntnorv.wrdl_holo.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;

public class GridIndicatorView extends View {
	/* FIELDS */
	private Paint indicatorPaint;
	private ArrayList<RectF> indicatorRectList;
	private ArrayList<Float> indicatorRotationList;
	
	private ArrayList<Point> highlightedTiles;
	
	private float tileWidth;
	private float tileHeight;
	private int rows;
	private int columns;
	
	private float indicatorHeight;
	
	private float[] rotMatrix;
	
	/* CONSTRUCOTRS */
	public GridIndicatorView(Context context) {
		super(context);

		// Constructor only for edit mode
		if (!this.isInEditMode()) {
			throw (new UnsupportedOperationException("GridIndicatorView(Context) is only supported in edit mode."));
		}
	}
	
	public GridIndicatorView(Context context, float tileWidth, float tileHeight, int columns, int rows) {
		super(context);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.rows = rows;
		this.columns = columns;
		
		initGridIndicatorView();
	}

	public GridIndicatorView(Context context, float tileWidth, float tileHeight, int columns, int rows,
							 float indicatorHeight, int indicatorColor) {
		this(context, tileWidth, tileHeight, rows, columns);
		
		this.indicatorHeight = indicatorHeight;
		this.indicatorPaint.setColor(indicatorColor);
	}

	/* INIT */
	private void initGridIndicatorView() {
		indicatorRectList = new ArrayList<RectF>();
		indicatorRotationList = new ArrayList<Float>();
		highlightedTiles = new ArrayList<Point>();
		
		indicatorPaint = new Paint();
		indicatorPaint.setColor(0xBB63BAF9);
		indicatorHeight = 16 * getResources().getDisplayMetrics().density;
		
		float rotValue = (float)Math.toDegrees(Math.atan(tileWidth/tileHeight));
		
		rotMatrix = new float[9];
		rotMatrix[4] = 0; // center
		
		rotMatrix[1] =  90; // top
		rotMatrix[7] = -90; // bottom
		rotMatrix[3] =   0; // left
		rotMatrix[5] = 180; // right
		
		rotMatrix[0] =  rotValue;       // top left
		rotMatrix[2] = -rotValue + 180; // top right
		rotMatrix[6] = -rotValue;       // bottom left
		rotMatrix[8] =  rotValue - 180; // bottom right
	}
	
	/* METHODS */
	/**
	 * Add a highlighted tile to the list. Backgrounds are drawn for every highlighted
	 * tile. When there are 2 or more highlighted tiles indicators are added
	 * automatically.
	 * @param tileX - highlighted tile column: {@code 0} to {@code (columns - 1)}
	 * @param tileY - highlighted tile row: {@code 0} to {@code (rows - 1)}
	 */
	public void addHighlightedTile (int tileX, int tileY) {
		highlightedTiles.add(0, new Point(tileX, tileY));
		
		if (highlightedTiles.size() > 1) {
			this.addIndicator(
					highlightedTiles.get(1).x, highlightedTiles.get(1).y,
					highlightedTiles.get(0).x, highlightedTiles.get(0).y);
		}
		
		invalidate();
	}
	
	public void removeLastHighlight() {
		if (highlightedTiles.size() > 0) {
			highlightedTiles.remove(0);
			invalidate();
		}
		
		removeLastIndicator();
	}
	
	public void clearHighlights() {
		highlightedTiles.clear();
		invalidate();
		
		clearIndicators();
	}
	
	/**
	 * Adds an indicator to the View. 
	 * Both {@code toCol} and {@code toRow} can't be equal to {@code fromCol} and
	 * {@code fromRow}.
	 * @param fromCol - indicator start tile column: {@code 0} to {@code (columns - 1)}
	 * @param fromRow - indicator start tile row: {@code 0} to {@code (rows - 1)}
	 * @param toCol - indicator end tile column: {@code fromCol +/- 1} or equal
	 * @param toRow - indicator end tile row: {@code fromRow +/- 1} or equal
	 */
	private void addIndicator (int fromCol, int fromRow, int toCol, int toRow) {
		int dX = fromCol - toCol;
		int dY = fromRow - toRow;
		
		float x, y, w, h;
		h = indicatorHeight;
		
		if (dX != 0 && dY != 0) {
			w = (float) Math.sqrt(tileWidth*tileWidth + tileHeight*tileHeight);
		} else {
			w = tileWidth;
		}
		
		x = fromCol * tileWidth + tileWidth/2;
		y = fromRow * tileHeight + tileHeight/2 - h/2;
		
		indicatorRectList.add(0, new RectF(x, y, x+w, y+h));
		indicatorRotationList.add(0, rotMatrix[(dY+1)*3 + dX + 1]);
		
		invalidate();
	}
	
	private void removeLastIndicator() {
		if (indicatorRectList.size() > 0) {
			indicatorRectList.remove(0);
			indicatorRotationList.remove(0);
			invalidate();
		}
	}
	
	private void clearIndicators() {
		indicatorRectList.clear();
		indicatorRotationList.clear();
		invalidate();
	}
	
	/* SETTERS */
	/**
	 * Set the height of an indicator. NOTE: all indicators must be
	 * cleared with {@link #clearIndicators()} and readded with
	 * {@link #addIndicator(int, int, int, int)} to change the height.
	 */
	public void setIndicatorHeight(float indicatorHeight) {
		this.indicatorHeight = indicatorHeight;
		invalidate();
	}
	
	public void setIndicatorColor(int color) {
		indicatorPaint.setColor(color);
		invalidate();
	}
	
	public void setWidth(float width) {
		tileWidth = width/columns;
		clearIndicators();
		initGridIndicatorView();
	}
	
	public void setHeight(float height) {
		tileHeight = height/rows;
		clearIndicators();
		initGridIndicatorView();
	}
	
	/* MEASURE */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int)tileWidth*columns + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
	}
	
	private int measureHeight(int measureSpec) {
		int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int)tileHeight*rows + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
	}

	/* DRAW */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for (int i = 0; i < indicatorRectList.size(); i++) {
			RectF indicator = indicatorRectList.get(i);
			canvas.save();
			canvas.rotate(indicatorRotationList.get(i), indicator.left, indicator.top + indicator.height()/2);
			canvas.drawRect(indicator, indicatorPaint);
			canvas.restore();
		}
		
		for (Point tile: highlightedTiles) {
			canvas.drawCircle(
					tile.x * tileWidth + tileWidth/2, tile.y * tileHeight + tileHeight/2,
					tileWidth/2 * 0.75f, indicatorPaint);
		}
	}
}
