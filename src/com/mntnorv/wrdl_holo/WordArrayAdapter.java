package com.mntnorv.wrdl_holo;

import java.util.List;

import com.mntnorv.wrdl_holo.dict.WordChecker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WordArrayAdapter extends ArrayAdapter<String> {
	private int scoreId;
	private WordChecker wordChecker;
	
	public WordArrayAdapter (Context context, int layoutId, int wordId, int scoreId, List<String> itemList) {
		super(context, layoutId, wordId, itemList);
		
		this.scoreId = scoreId;
	}
	
	public void setWordChecker(WordChecker checker) {
		this.wordChecker = checker;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View returnedView = super.getView(position, convertView, parent);
		
		if (wordChecker != null) {
			TextView scoreField = (TextView)returnedView.findViewById(scoreId);
			String test = "+" + Integer.toString(wordChecker.getWordScore(super.getItem(position)));
			scoreField.setText(test);
			returnedView.invalidate();
		}
		
		return returnedView;
	}
}
