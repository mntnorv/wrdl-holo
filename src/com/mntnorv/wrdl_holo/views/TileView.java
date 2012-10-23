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
import android.view.ViewGroup;

import com.mntnorv.wrdl_holo.R;

public class TileView extends View {
	/* VARS */
	private RectF mainTileRect;
	private RectF borderRect;
	private Paint tilePaint;
	private Paint borderPaint;
	private Paint tileTextPaint;
	
	private String tileText;
	
	private int defaultColor;
	private int highlightedColor;
	private boolean highlighted;
	
	private Rect textBounds;
	private float tileSize;
	
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
        
        int tileSize = a.getDimensionPixelSize(R.styleable.TileView_tileSize, 0);
        if (tileSize != 0) {
        	setSize(tileSize);
        }
        
        int textSize = a.getDimensionPixelSize(R.styleable.TileView_android_textSize, 0);
        if (textSize != 0) {
        	setTextSize(textSize);
        }
        
        a.recycle();
	}
	
	/* METHODS */
	private void initTileView() {
		// Default values
		tileSize = 48;
		tileSize = 48;
		defaultColor = 0xFFAAAAAA;
		highlightedColor = 0xFF63BEF7;
		tileText = "A";
		highlighted = false;
		
		// Default tile paint
		tilePaint = new Paint();
		tilePaint.setAntiAlias(true);
		tilePaint.setColor(defaultColor);
		
		// Default border paint
		borderPaint = new Paint();
		borderPaint.setAntiAlias(true);
		borderPaint.setColor(0x66000000);
		
		// Default text paint
		tileTextPaint = new Paint();
		tileTextPaint.setAntiAlias(true);
		tileTextPaint.setSubpixelText(true);
		tileTextPaint.setColor(0xFF000000);
		tileTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
		tileTextPaint.setTextAlign(Align.CENTER);
		
		// Get text bounds
		textBounds = new Rect();
		tileTextPaint.getTextBounds(tileText, 0, tileText.length(), textBounds);
		
		updateRectangle();
	}
	
	private void updateRectangle() {
		float border = 1/18.0f;
		mainTileRect = new RectF(0.1f*tileSize, 0.1f*tileSize, 0.9f*tileSize, 0.9f*tileSize);
		borderRect = new RectF(border*tileSize, border*tileSize, (1f-border)*tileSize, (1f-border)*tileSize);
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
	
	public void setSize(float size) {
		tileSize = size;
		updateRectangle();
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
		ViewGroup.LayoutParams lp = this.getLayoutParams();
		
		int width = measureWidth(widthMeasureSpec, lp.width);
		int height = measureHeight(heightMeasureSpec, lp.height);
		int size = Math.min(width, height);
		
		this.setSize(size);
		
		setMeasuredDimension(size, size);
	}
	
	private int measureWidth(int measureSpec, int layoutParam) {
		int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // Measure the text
            result = (int)tileSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
            	if (layoutParam == ViewGroup.LayoutParams.MATCH_PARENT) {
            		result = Math.max(result, specSize);
            	} else if (layoutParam != 0) {
            		result = Math.min(result, specSize);
            	}
            }
        }

        return result;
	}
	
	private int measureHeight(int measureSpec, int layoutParam) {
		int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // Measure the text
            result = (int)tileSize + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
            	if (layoutParam == ViewGroup.LayoutParams.MATCH_PARENT) {
            		result = Math.max(result, specSize);
            	} else if (layoutParam != 0) {
            		result = Math.min(result, specSize);
            	}
            }
        }

        return result;
	}
	
	@Override
	public ViewGroup.LayoutParams getLayoutParams() {
		return new ViewGroup.MarginLayoutParams(super.getLayoutParams());	
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
		
		canvas.drawRoundRect(borderRect, 4, 4, tilePaint);
		canvas.drawRoundRect(borderRect, 4, 4, borderPaint);
		canvas.drawRect(mainTileRect, tilePaint);
		
		canvas.drawText(tileText, tileSize/2, (tileSize + textBounds.bottom - textBounds.top)/2, tileTextPaint);
	}
}
