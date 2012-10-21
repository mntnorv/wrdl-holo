package com.mntnorv.wrdl_holo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.mntnorv.wrdl_holo.R;

public class TileView extends View {
	/* VARS */
	private RectF tileRect;
	private Paint tilePaint;
	private String tileText;
	private Paint tileTextPaint;
	
	private int defaultColor;
	private int highlightedColor;
	private boolean highlighted;
	
	private Rect textBounds;
	private float tileWidth;
	private float tileHeight;
	
	/* CONSTRUCTORS */
	public TileView(Context context) {
		super(context);
		initTileView();
	}

	public TileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTileView();
		
		TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TileView);
		
		CharSequence s = a.getString(R.styleable.TileView_android_text);
        if (s != null) {
            setText(s.toString());
        }
        
        setTextColor(a.getColor(R.styleable.TileView_android_textColor, 0xFF000000));
        
        setColor(a.getColor(R.styleable.TileView_android_background, 0x00000000));
        
        int tileWidth = a.getDimensionPixelSize(R.styleable.TileView_tileWidth, 0);
        if (tileWidth != 0) {
        	setWidth(tileWidth);
        }
        
        int tileHeight = a.getDimensionPixelSize(R.styleable.TileView_tileHeight, 0);
        if (tileHeight != 0) {
        	setWidth(tileHeight);
        }
        
        int textSize = a.getDimensionPixelSize(R.styleable.TileView_android_textSize, 0);
        if (textSize != 0) {
        	setTextSize(textSize);
        }
        
        a.recycle();
	}
	
	/* METHODS */
	private void initTileView() {
		tileWidth = 48;
		tileHeight = 48;
		tileRect = new RectF(0, 0, 48, 48);
		defaultColor = 0xFFAAAAAA;
		highlightedColor = 0xFF63BEF7;
		highlighted = false;
		tilePaint = new Paint();
		tilePaint.setAntiAlias(true);
		tilePaint.setARGB(255, 100, 100, 100);
		tileText = "A";
		tileTextPaint = new Paint();
		tileTextPaint.setAntiAlias(true);
		tileTextPaint.setSubpixelText(true);
		tileTextPaint.setARGB(255, 0, 0, 0);
		tileTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
		tileTextPaint.setTextAlign(Align.CENTER);
		textBounds = new Rect();
		tileTextPaint.getTextBounds(tileText, 0, tileText.length(), textBounds);
	}
	
	/* SETTERS */
	public void setColor(int color) {
		defaultColor = color;
		invalidate();
	}
	
	public void setHighlightColor(int color) {
		highlightedColor = color;
		invalidate();
	}
	
	public void setWidth(float width) {
		tileWidth = width;
		tileRect = new RectF(0, 0, tileWidth, tileHeight);
		requestLayout();
		invalidate();
	}
	
	public void setHeight(float height) {
		tileHeight = height;
		tileRect = new RectF(0, 0, tileWidth, tileHeight);
		requestLayout();
		invalidate();
	}
	
	public void setText(String text) {
		tileText = text;
		tileTextPaint.getTextBounds(tileText, 0, tileText.length(), textBounds);
		invalidate();
	}
	
	public void setTextColor(int color) {
		tileTextPaint.setColor(color);
		invalidate();
	}
	
	public void setTextSize(int size) {
		tileTextPaint.setTextSize(size);
		tileTextPaint.getTextBounds(tileText, 0, tileText.length(), textBounds);
		invalidate();
	}
	
	public void setHighlighted (boolean value) {
		highlighted = value;
		invalidate();
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
            // Measure the text
            result = (int)tileWidth + getPaddingLeft() + getPaddingRight();
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
            // Measure the text
            result = (int)tileHeight + getPaddingTop() + getPaddingBottom();
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
		
		if (!highlighted) {
			tilePaint.setColor(defaultColor);
		} else {
			tilePaint.setColor(highlightedColor);
		}
		
		canvas.drawRect(tileRect, tilePaint);
		canvas.drawText(tileText, tileWidth/2, (tileHeight + textBounds.bottom - textBounds.top)/2, tileTextPaint);
	}
}
