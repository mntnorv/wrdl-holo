package com.mntnorv.wrdl_holo.dict;

import java.io.IOException;

import android.content.Context;
import android.util.Log;

public final class DictionaryProvider {
	private static Dictionary dict;
	private static boolean isDictOpened = false;
	private static String dictFileName = "dict.hex";
	
	public static Dictionary getDictionary(Context context) {
		if (!isDictOpened) {
			loadDictionary(context);
		}
		
		return dict;
	}
	
	public static void loadDictionary(Context context) {
		try {
			dict = new Dictionary(context.getAssets().open(dictFileName));
			isDictOpened = true;
		} catch (IOException e) {
			Log.e("dictionary", "Error loading dictionary from \"" + dictFileName + "\"");
		}
	}
}
