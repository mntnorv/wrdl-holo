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
	private static final int PROP_LAYOUT_ID = R.layout.game_state_item_property;
	private static final int BIG_PROP_LAYOUT_ID = R.layout.game_state_item_big_property;
	private static final int GRID_ID = R.id.gameGridView;
	private static final int ITEM_ID = R.id.game_state_item;
	private static final int ITEM_PROP_NAME_ID = R.id.item_property_name;
	private static final int ITEM_PROP_CONTENT_ID = R.id.item_property_content;
	private static final int PROP_CONTAINER_ID = R.id.game_state_item_properties;
	private static final int BIG_PROP_CONTAINER_ID = R.id.game_state_item_big_properties;
	
	private List<GameState> gameStateList;
	private LayoutInflater inflater;
	private Context context;
	
	public MainMenuAdapter(Context context, List<GameState> objects) {
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
		TileGridView grid = null;
		
		if (convertView == null) {
			convertView = inflater.inflate(ITEM_LAYOUT_ID, parentView, false);
			grid = (TileGridView)convertView.findViewById(GRID_ID);
			grid.create(gameStateList.get(index).getSize());
		} else if (convertView.getId() == ITEM_ID) {
			convertView = inflater.inflate(ITEM_LAYOUT_ID, parentView, false);
		}
		
		if (grid == null) {
			grid = (TileGridView)convertView.findViewById(GRID_ID);
		}
		
		ViewGroup propContainer = (ViewGroup) convertView.findViewById(PROP_CONTAINER_ID);
		ViewGroup bigPropContainer = (ViewGroup) convertView.findViewById(BIG_PROP_CONTAINER_ID);
		
		grid.setLetters(gameStateList.get(index).getLetterArray());
		//name.setText(Integer.toString(gameStateList.get(index).getWordCount()));
		//FontUtils.setRobotoFont(context, name, true);
		
		propContainer.addView(
				getNewProp("gamemode", "Infty", propContainer));
		
		bigPropContainer.addView(
				getNewBigProp("words found", "1/123", bigPropContainer));
		
		return convertView;
	}
	
	private View getNewProp(String name, String content, ViewGroup parent) {
		View propView = inflater.inflate(PROP_LAYOUT_ID, parent, false);
		TextView nameView = (TextView) propView.findViewById(ITEM_PROP_NAME_ID);
		TextView contentView = (TextView) propView.findViewById(ITEM_PROP_CONTENT_ID);
		
		nameView.setText(name);
		contentView.setText(content);
		
		return propView;
	}
	
	private View getNewBigProp(String name, String content, ViewGroup parent) {
		View propView = inflater.inflate(BIG_PROP_LAYOUT_ID, parent, false);
		TextView nameView = (TextView) propView.findViewById(ITEM_PROP_NAME_ID);
		TextView contentView = (TextView) propView.findViewById(ITEM_PROP_CONTENT_ID);
		
		nameView.setText(name);
		contentView.setText(content);
		
		return propView;
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
