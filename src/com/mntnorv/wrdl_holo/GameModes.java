package com.mntnorv.wrdl_holo;

public class GameModes {
	private static final int[] NAME_RESOURCE_IDS = {
		R.string.game_mode_infinity
	};
	
	public static final int INFINITY = 0;
	
	public static int getGamemodeNameResource(int gamemode) {
		if (gamemode >= 0 && gamemode < NAME_RESOURCE_IDS.length) {
			return NAME_RESOURCE_IDS[gamemode];
		} else {
			throw new IllegalArgumentException(
					String.format("gamemode must be between 0 and %d",
							NAME_RESOURCE_IDS.length));
		}
	}
}
