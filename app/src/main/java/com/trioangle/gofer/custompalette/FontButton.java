package com.trioangle.gofer.custompalette;
/**
 * @package com.trioangle.gofer
 * @subpackage custompalette
 * @category FontButton
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/* ************************************************************
                CustomFontUtils
Used for custom button when you want use custom fonts in button
*************************************************************** */
public class FontButton extends Button {

    // Constructor
    public FontButton(Context context) {
        super(context);

        CustomFontUtils.applyCustomFont(this, context, null);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CustomFontUtils.applyCustomFont(this, context, attrs);
    }
}