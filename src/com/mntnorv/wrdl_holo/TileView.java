package com.mntnorv.wrdl_holo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TileView extends View {
	/* VARS */
	private Rect tileRect;
	private Paint tilePaint;
	private String tileText;
	private Paint tileTextPaint;
	
	private Rect textBounds;
	
	private int tileSize;
	
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
        
        int tileSize = a.getDimensionPixelSize(R.styleable.TileView_size, 0);
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
		tileSize = 48;
		tileRect = new Rect(0, 0, 48, 48);
		tilePaint = new Paint();
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
		tilePaint.setColor(color);
	}
	
	public void setSize(int size) {
		tileSize = size;
		tileRect = new Rect(0, 0, tileSize, tileSize);
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
	
	/* MEASURE */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
		//setMeasuredDimension(tileSize, tileSize);
	}
	
	private int measureWidth(int measureSpec) {
		int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
            Log.d("width", "exactly " + Integer.toString(specSize));
        } else {
            // Measure the text
            result = tileSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
                Log.d("width", "at most " + Integer.toString(specSize));
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
            Log.d("height", "exactly " + Integer.toString(specSize));
        } else {
            // Measure the text
            result = tileSize + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
                Log.d("height", "at most " + Integer.toString(specSize));
            }
        }

        return result;
	}
	
	/* DRAW */
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("size", Integer.toString(textBounds.bottom - textBounds.top));
		super.onDraw(canvas);
		canvas.drawRect(tileRect, tilePaint);
		canvas.drawText(tileText, tileSize/2, (tileSize + textBounds.bottom - textBounds.top)/2, tileTextPaint);
	}
}
