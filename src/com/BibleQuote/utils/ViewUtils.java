package com.BibleQuote.utils;

import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import com.BibleQuote.R;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 02.08.12
 * Time: 23:03
 * To change this template use File | Settings | File Templates.
 */
public class ViewUtils {

	public static void setActionBarBackground(SherlockFragmentActivity context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			BitmapDrawable bg = (BitmapDrawable) context.getResources().getDrawable(R.drawable.action_bar_bg);
			bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			context.getSupportActionBar().setBackgroundDrawable(bg);
		}
	}

	public static void setActionBarBackground(SherlockActivity context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			BitmapDrawable bg = (BitmapDrawable) context.getResources().getDrawable(R.drawable.action_bar_bg);
			bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			context.getSupportActionBar().setBackgroundDrawable(bg);
		}
	}

}
