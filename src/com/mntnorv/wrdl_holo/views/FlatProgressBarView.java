package com.mntnorv.wrdl_holo.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class FlatProgressBarView extends View {
	
	private float width;
	private float height;
	
	private float progress;
	private float maxProgress;
	
	private Paint backgroundPaint;
	private Paint foregroundPaint;
	private Paint textPaint;
	
	private Rect textBounds;
	
	private String progressText;

	public FlatProgressBarView(Context context) {
		super(context);
		initProgressBar();
	}

	public FlatProgressBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initProgressBar();
	}

	private void initProgressBar() {
		width = 120;
		height = 30;
		progress = 0;
		maxProgress = 100;
		
		progressText = "0";
		
		// Default background paint
		backgroundPaint = new Paint();
		backgroundPaint.setColor(0x00000000);
		
		// Default foreground paint
		foregroundPaint = new Paint();
		foregroundPaint.setColor(0xFF000000);
		
		// Default text paint
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setSubpixelText(true);
		textPaint.setColor(0xFF000000);
		textPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
		textPaint.setTextAlign(Align.CENTER);
		
		// Get text bounds
		textBounds = new Rect();
		textPaint.getTextBounds(progressText, 0, progressText.length(), textBounds);
	}
}
