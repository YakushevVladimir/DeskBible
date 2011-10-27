/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

	private final static String TAG = "Share";
	private static Context mContext;
	private static SharedPreferences preference;
	
	static public void Init(Context context) {
		mContext = context;
		preference = PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	static public String restoreStateString(String key) {
		Log.i(TAG, "restoreStateString(" + key + ")");
		if (preference == null) {
			return null;
		}
		return preference.getString(key, null);
	}

	static public void saveStateString(String key, String v) {
		Log.i(TAG, "saveStateString(" + key + ", " + v + ")");
		if (preference == null) {
			return;
		}
		preference.edit().putString(key, v).commit();
	}
	
	static public Boolean restoreStateBoolean(String key) {
		Log.i(TAG, "restoreStateBoolean(" + key + ")");
		if (preference == null) {
			return false;
		}
		return preference.getBoolean(key, false);
	}
	
	static public void saveStateBoolean(String key, Boolean v) {
		Log.i(TAG, "saveStateBoolean(" + key + ", " + v + ")");
		if (preference == null) {
			return;
		}
		preference.edit().putBoolean(key, v).commit();
	}
	
	static public String getTextSize() {
		if (preference == null) {
			return "12";
		}
		return String.valueOf(preference.getInt("TextSize", 12));
	}

	static public String getTextColor() {
		if (preference == null) {
			return "#ff000000";
		}
		return preference.getString("TextColor", "#ff000000");
	}

	static public String getTextBackground() {
		if (preference == null) {
			return "#ffffffff";
		}
		return preference.getString("TextBG", "#ffffffff");
	}

}
