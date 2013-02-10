package com.mntnorv.wrdl_holo;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mntnorv.wrdl_holo.dict.ScoreCounter;

public class WordArrayAdapter extends ArrayAdapter<String> {
	private int scoreId;
	private ScoreCounter scoreCounter;
	
	public WordArrayAdapter (Context context, int layoutId, int wordId, int scoreId, List<String> itemList) {
		super(context, layoutId, wordId, itemList);
		
		this.scoreId = scoreId;
	}
	
	public void setScoreCounter(ScoreCounter counter) {
		this.scoreCounter = counter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View returnedView = super.getView(position, convertView, parent);
		
		if (scoreCounter != null) {
			TextView scoreField = (TextView)returnedView.findViewById(scoreId);
			String test = "+" + Integer.toString(scoreCounter.getWordScore(super.getItem(position)));
			scoreField.setText(test);
			returnedView.invalidate();
		}
		
		return returnedView;
	}
}
