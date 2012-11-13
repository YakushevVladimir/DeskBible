/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.utils;

import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import com.BibleQuote.R;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 02.08.12
 * Time: 23:03
 * To change this template use File | Settings | File Templates.
 */
public class ViewUtils {

	public static void setActionBarBackground(SherlockActivity context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			BitmapDrawable bg = (BitmapDrawable)context.getResources().getDrawable(R.drawable.action_bar_bg);
			bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			context.getSupportActionBar().setBackgroundDrawable(bg);

//			BitmapDrawable bgSplit = (BitmapDrawable)context.getResources().getDrawable(R.drawable.action_bar_divider);
//			bgSplit.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
//			context.getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
		}
	}

}
