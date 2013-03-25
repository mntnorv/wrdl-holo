package com.mntnorv.wrdl_holo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontUtils {

    public static interface FontTypes {
        public static String LIGHT = "Light";
        public static String REGULAR = "Regular";
        public static String BOLD = "Bold";
    }

    private static Map<String, String> fontMap = new HashMap<String, String>();

    static {
        fontMap.put(FontTypes.LIGHT, "Roboto-Light.ttf");
        fontMap.put(FontTypes.REGULAR, "Roboto-Regular.ttf");
        fontMap.put(FontTypes.BOLD, "Roboto-Bold.ttf");
    }

    private static Map<String, Typeface> typefaceCache = new HashMap<String, Typeface>();

    private static Typeface getRobotoTypeface(Context context, String fontType) {
        String fontPath = fontMap.get(fontType);
        if (!typefaceCache.containsKey(fontType)){
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        return typefaceCache.get(fontType);
    }

    private static Typeface getRobotoTypeface(Context context, Typeface originalTypeface, boolean light) {
    	String robotoFontType = FontTypes.REGULAR;
    	if (light) {
    		robotoFontType = FontTypes.LIGHT;
    	}
    	
        if (originalTypeface != null) {
            int style = originalTypeface.getStyle();
            switch (style) {
            case Typeface.BOLD:
                robotoFontType = FontTypes.BOLD;
                break;
            }
        }

        return getRobotoTypeface(context, robotoFontType);
    }

    public static void setRobotoFont(Context context, View view, boolean light) {
        if (view instanceof ViewGroup){
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++){
                setRobotoFont(context, ((ViewGroup)view).getChildAt(i), light);
            }
        } else if (view instanceof TextView) {
            Typeface currentTypeface = ((TextView) view).getTypeface();
            ((TextView) view).setTypeface(getRobotoTypeface(context, currentTypeface, light));
        }
    }
}