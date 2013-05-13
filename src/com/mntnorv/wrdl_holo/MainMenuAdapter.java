package com.mntnorv.wrdl_holo;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mntnorv.wrdl_holo.views.FlatProgressBarView;
import com.mntnorv.wrdl_holo.views.TileGridView;

public class MainMenuAdapter extends BaseAdapter {
	
	private List<GameState> gameStateList;
	private SparseBooleanArray activationStates;
	private LayoutInflater inflater;
	private Context context;
	
	private String gamemodeString;
	private String pointsString;
	private String wordsString;
	
	public MainMenuAdapter(Context context, List<GameState> objects) {
		this.context = context;
		this.gameStateList = objects;
		
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		gamemodeString = context.getResources().getString(GAMEMODE_STRING_ID);
		pointsString = context.getResources().getString(POINTS_STRING_ID);
		wordsString = context.getResources().getString(WORDS_STRING_ID);
		
		activationStates = new SparseBooleanArray();
	}

	@Override
	public int getCount() {
		return gameStateList.size() + 1;
	}

	@Override
	public Object getItem(int index) {
		return gameStateList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index - 1;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parentView) {
		View returnView = null;
		
		switch (getItemViewType(index)) {
		case NEW_GAME_BUTTON_TYPE:
			returnView = getNewGameButton(index, convertView, parentView);
			break;
		case GAME_STATE_ITEM_TYPE:
			returnView = getGameStateView(index, convertView, parentView);
			break;
		}
		
		return returnView;
	}
	
	public void activateGameState(int stateId) {
		activationStates.put(stateId, true);
		notifyDataSetChanged();
	}
	
	public void activateAllGameStates() {
		activationStates.clear();
		for (GameState gameState: gameStateList) {
			activationStates.put(gameState.getId(), true);
		}
		notifyDataSetChanged();
	}
	
	public void deactivateGameState(int stateId) {
		activationStates.delete(stateId);
		notifyDataSetChanged();
	}
	
	public void deactivateAllGameStates() {
		activationStates.clear();
		notifyDataSetChanged();
	}
	
	public void toggleGameStateActivation(int stateId) {
		if (isGameStateActivated(stateId)) {
			deactivateGameState(stateId);
		} else {
			activateGameState(stateId);
		}
	}
	
	public boolean isGameStateActivated(int stateId) {
		return activationStates.get(stateId, false);
	}
	
	public boolean isAnyGameStateActivated() {
		return activationStates.size() > 0;
	}
	
	private View getGameStateView(int index, View convertView, ViewGroup parentView) {
		TileGridView grid = null;
		GameState currentGameState = gameStateList.get((int) getItemId(index));
		
		if (convertView == null) {
			convertView = inflater.inflate(ITEM_LAYOUT_ID, parentView, false);
			grid = (TileGridView)convertView.findViewById(GRID_ID);
			grid.create(currentGameState.getSize());
		} else if (convertView.getId() != ITEM_ID) {
			convertView = inflater.inflate(ITEM_LAYOUT_ID, parentView, false);
			grid = (TileGridView)convertView.findViewById(GRID_ID);
			grid.create(currentGameState.getSize());
		}
		
		if (grid == null) {
			grid = (TileGridView)convertView.findViewById(GRID_ID);
		}
		
		ViewGroup propContainer = (ViewGroup) convertView.findViewById(PROP_CONTAINER_ID);
		ViewGroup bigPropContainer = (ViewGroup) convertView.findViewById(BIG_PROP_CONTAINER_ID);
		FlatProgressBarView progressBar = (FlatProgressBarView) convertView.findViewById(R.id.menu_item_progress_bar);
		
		String gamemodeName = context.getResources().getString(
				GameModes.getGamemodeNameResource(currentGameState.getGamemode()));
		String points = Integer.toString(currentGameState.getScore());
		String words = String.format(Locale.US, "%d/%d", currentGameState.getGuessedWordCount(),
				currentGameState.getWordCount());
		
		grid.setLetters(currentGameState.getLetterArray());
		
		propContainer.removeAllViews();
		propContainer.addView(getNewProp(gamemodeString, gamemodeName, propContainer));
		propContainer.addView(getNewProp(pointsString, points, propContainer));
		bigPropContainer.removeAllViews();
		bigPropContainer.addView(getNewBigProp(wordsString, words, bigPropContainer));
		
		progressBar.setMaxProgress(currentGameState.getWordCount());
		progressBar.setProgress(currentGameState.getGuessedWordCount());
		
		convertView.setActivated(activationStates.get(currentGameState.getId(), false));
		
		return convertView;
	}
	
	private View getNewGameButton(int index, View convertView, ViewGroup parentView) {
		if (convertView == null) {
			convertView = inflater.inflate(MENU_BUTTON_LAYOUT_ID, parentView, false);
		} else if (convertView.getId() != MENU_BUTTON_TEXT_ID) {
			convertView = inflater.inflate(MENU_BUTTON_LAYOUT_ID, parentView, false);
		}
		
		TextView buttonName = (TextView) convertView.findViewById(MENU_BUTTON_TEXT_ID);
		buttonName.setText(context.getResources().getString(NEW_GAME_STRING));
		FontUtils.setRobotoFont(context, buttonName, true);
		
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
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getItemViewType(int pos) {
		if (pos > 0) {
			return GAME_STATE_ITEM_TYPE;
		} else {
			return NEW_GAME_BUTTON_TYPE;
		}
	}

	@Override
	public int getViewTypeCount(){
		return VIEW_TYPE_COUNT;
	}
	
	private static final int VIEW_TYPE_COUNT = 2;
	
	public static final int NEW_GAME_BUTTON_TYPE = 0;
	public static final int GAME_STATE_ITEM_TYPE = 1;
	
	private static final int ITEM_LAYOUT_ID = R.layout.game_state_item;
	private static final int PROP_LAYOUT_ID = R.layout.game_state_item_property;
	private static final int BIG_PROP_LAYOUT_ID = R.layout.game_state_item_big_property;
	private static final int MENU_BUTTON_LAYOUT_ID = R.layout.menu_button;
	private static final int GRID_ID = R.id.gameGridView;
	private static final int ITEM_ID = R.id.game_state_item;
	private static final int ITEM_PROP_NAME_ID = R.id.item_property_name;
	private static final int ITEM_PROP_CONTENT_ID = R.id.item_property_content;
	private static final int PROP_CONTAINER_ID = R.id.game_state_item_properties;
	private static final int BIG_PROP_CONTAINER_ID = R.id.game_state_item_big_properties;
	private static final int MENU_BUTTON_TEXT_ID = R.id.menu_button_text;
	
	private static final int GAMEMODE_STRING_ID = R.string.item_prop_gamemode;
	private static final int POINTS_STRING_ID = R.string.item_prop_points;
	private static final int WORDS_STRING_ID = R.string.item_prop_words;
	private static final int NEW_GAME_STRING = R.string.new_game_button;
}
