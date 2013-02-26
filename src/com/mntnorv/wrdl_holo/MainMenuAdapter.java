package com.mntnorv.wrdl_holo;

import java.util.List;

import com.mntnorv.wrdl_holo.views.TileGridView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainMenuAdapter extends BaseAdapter {

	private static final int ITEM_LAYOUT_ID = R.layout.game_state_item;
	private static final int GRID_ID = R.id.gameGridView;
	private static final int NAME_ID = R.id.itemName;
	
	private List<GameState> gameStateList;
	private Context context;
	private LayoutInflater inflater;
	
	public MainMenuAdapter(Context context, List<GameState> objects) {
		super();
		this.context = context;
		this.gameStateList = objects;
		
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return gameStateList.size();
	}

	@Override
	public Object getItem(int index) {
		return gameStateList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parentView) {
		convertView = inflater.inflate(ITEM_LAYOUT_ID, parentView, false);
		TileGridView grid = (TileGridView)convertView.findViewById(GRID_ID);
		TextView name = (TextView)convertView.findViewById(NAME_ID);
		
		grid.create(gameStateList.get(index).getSize());
		grid.setLetters(gameStateList.get(index).getLetterArray());
		name.setText(Integer.toString(gameStateList.get(index).getGuessedWordCount()));
		
		return convertView;
	}

	@Override
	public boolean hasStableIds(){
		return true;
	}

	@Override
	public int getItemViewType(int pos){
		return IGNORE_ITEM_VIEW_TYPE;
	}

	@Override
	public int getViewTypeCount(){
		return 1;
	}
}
