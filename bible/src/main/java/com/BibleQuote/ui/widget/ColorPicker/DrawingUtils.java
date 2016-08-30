package com.BibleQuote.ui.widget.ColorPicker;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 03.2016
 */
public final class DrawingUtils {

    private DrawingUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static int dpToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
        int res = (int)(val + 0.5);
        if(res == 0 && val > 0) {
            res = 1;
        }
        return res;
    }
}
