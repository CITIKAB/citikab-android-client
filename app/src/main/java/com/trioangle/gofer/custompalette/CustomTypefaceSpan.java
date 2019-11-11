package com.trioangle.gofer.custompalette;

/**
 * @package com.trioangle.gofer
 * @subpackage custompalette
 * @category CustomTypefaceSpan
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/* ************************************************************
                CustomTypefaceSpan
Used for spannable string in main activity 
*************************************************************** */
@SuppressLint("ParcelCreator")
public class CustomTypefaceSpan extends TypefaceSpan {

    private final Typeface newType;

    /**
     * Typefacespan
     */
    public CustomTypefaceSpan(String family, Typeface type) {
        super(family);
        newType = type;
    }

    /**
     * Apply Typeface
     */
    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf); // Set Type space
    }

    /**
     * Draw Textpaint
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    /**
     * Measure states of textpaint
     */
    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }
}
