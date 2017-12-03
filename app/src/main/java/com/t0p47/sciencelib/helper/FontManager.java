package com.t0p47.sciencelib.helper;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by 01 on 17.10.2017.
 */

public class FontManager {

    public static final String ROOT = "fonts/",
    FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context,String font){
        return Typeface.createFromAsset(context.getAssets(), font);
    }

}
