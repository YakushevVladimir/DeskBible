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

public class Share {

	private final static String TAG = "Share";
	
	static public String restoreStateString(Context context, String key) {
		Log.i(TAG, "restoreStateString(" + key + ")");
		SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(context);
		return Settings.getString(key, null);
	}

	static public void saveStateString(Context context, String key, String v) {
		Log.i(TAG, "saveStateString(" + key + ", " + v + ")");
		SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(context);
		Settings.edit().putString(key, v).commit();
	}
	
	static public Boolean restoreStateBoolean(Context context, String key) {
		Log.i(TAG, "restoreStateBoolean(" + key + ")");
		SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(context);
		return Settings.getBoolean(key, false);
	}
	
	static public void saveStateBoolean(Context context, String key, Boolean v) {
		Log.i(TAG, "saveStateBoolean(" + key + ", " + v + ")");
		SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(context);
		Settings.edit().putBoolean(key, v).commit();
	}
}
