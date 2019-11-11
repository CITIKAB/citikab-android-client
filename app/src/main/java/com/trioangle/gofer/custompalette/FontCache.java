package com.trioangle.gofer.custompalette;
/**
 * @package com.trioangle.gofer
 * @subpackage custompalette
 * @category FontCache
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/* ************************************************************
                FontCache
Used for custom font from assets folder
*************************************************************** */
public class FontCache {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    /**
     * Return typeface font
     */
    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontname);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontname, typeface);
        }

        return typeface;
    }
}