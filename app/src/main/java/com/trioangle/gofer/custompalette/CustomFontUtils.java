package com.trioangle.gofer.custompalette;

/**
 * @package com.trioangle.gofer
 * @subpackage custompalette
 * @category customfonts
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trioangle.gofer.R;
/* ************************************************************
                CustomFontUtils
Used for user custom fonts if you want to change the font name to change from string file
*************************************************************** */

public class CustomFontUtils {

    /**
     * Apply for custom fonts
     */
    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView);

        String fontName = attributeArray.getString(R.styleable.FontTextView_fontName);

        Typeface customFont = selectTypeface(context, fontName);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    /**
     * Apply for custom fonts
     */
    public static void applyCustomFont(EditText customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView);

        String fontName = attributeArray.getString(R.styleable.FontTextView_fontName);


        Typeface customFont = selectTypeface(context, fontName);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    /**
     * Apply for custom fonts
     */
    public static void applyCustomFont(Button customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView);

        String fontName = attributeArray.getString(R.styleable.FontTextView_fontName);

        Typeface customFont = selectTypeface(context, fontName);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    /**
     * Select font typeface
     */
    private static Typeface selectTypeface(Context context, String fontName) {
        if (fontName.contentEquals(context.getResources().getString(R.string.font_PermanentMarker))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_PermanentMarker), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_Book))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_Book), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_Medium))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_Medium), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_NarrBook))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_NarrBook), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_NarrMedium))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_NarrMedium), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_NarrNews))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_NarrNews), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_News))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_News), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_WideMedium))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_WideMedium), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_WideNews))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_WideNews), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_UBERBook))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERBook), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_UBERMedium))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_UBERNews))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERNews), context);
        } else if (fontName.contentEquals(context.getResources().getString(R.string.font_UberClone))) {
            return FontCache.getTypeface(context.getResources().getString(R.string.fonts_UberClone), context);
        } else {
            return null;
        }
    }
}
