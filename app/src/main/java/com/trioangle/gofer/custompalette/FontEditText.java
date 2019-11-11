package com.trioangle.gofer.custompalette;
/**
 * @package com.trioangle.gofer
 * @subpackage custompalette
 * @category FontEditText
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/* ************************************************************
                FontEditText
Used for custom Edit Text when you want use custom fonts in edit text
*************************************************************** */
public class FontEditText extends AppCompatEditText {

    public FontEditText(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}