/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: PreferenceHelper.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 *
 */
package com.BibleQuote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

public final class PreferenceHelper {

	public static final String KEY_VIEW_BOOK_VERSE = "always_view_verse_numbers";

    private final static String TAG = "Share";

	private static SharedPreferences preference;

	private PreferenceHelper() throws InstantiationException {
		throw new InstantiationException("This class is not for instantiation");
	}

	static public void Init(Context context) {
        preference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    static public String restoreStateString(String key) {
        Log.i(TAG, "restoreStateString(" + key + ")");
		if (preference == null) {
			return "";
		}
		return preference.getString(key, "");
	}

	static public void saveStateString(String key, String value) {
		Log.i(TAG, "saveStateString(" + key + ", " + value + ")");
		if (preference == null) {
			return;
		}
        preference.edit().putString(key, value).apply();
    }

	static public int restoreStateInt(String key) {
		Log.i(TAG, "restoreStateString(" + key + ")");
		if (preference == null) {
			return 0;
		}
		return preference.getInt(key, 0);
	}

	static public void saveStateInt(String key, int value) {
		Log.i(TAG, "saveStateString(" + key + ", " + value + ")");
		if (preference == null) {
			return;
		}
        preference.edit().putInt(key, value).apply();
    }

    @NonNull
    static public Boolean restoreStateBoolean(String key) {
        Log.i(TAG, "restoreStateBoolean(" + key + ")");
        return preference != null && preference.getBoolean(key, false);
    }

	static public void saveStateBoolean(String key, Boolean v) {
		Log.i(TAG, "saveStateBoolean(" + key + ", " + v + ")");
		if (preference == null) {
			return;
		}
        preference.edit().putBoolean(key, v).apply();
    }

	static public boolean isReadModeByDefault() {
        return preference != null && preference.getBoolean("ReadModeByDefault", false);
    }

	static public String getTextSize() {
		if (preference == null) {
			return "12";
		}
		return String.valueOf(preference.getInt("TextSize", 12));
	}

	static public String getTextColor() {
		if (preference == null) {
			return "#000000";
		}
		String color = preference.getString("TextColor", "#000000");
		return getWebColor(color);
	}

	public static String getTextBackground() {
		if (preference == null) {
			return "#ffffff";
		}
		String background = preference.getString("TextBG", "#ffffff");
		return getWebColor(background);
	}

	public static String getTextColorSelected() {
		if (preference == null) {
			return "#000000";
		}
		String colorSelected = preference.getString("TextColorSel", "#000000");
		return getWebColor(colorSelected);
	}

	public static String getTextBackgroundSelected() {
		if (preference == null) {
			return "#FEF8C4";
		}
		String backgroundSelected = preference.getString("TextBGSel", "#FEF8C4");
		return getWebColor(backgroundSelected);
	}

	public static Integer getHistorySize() {
		if (preference == null) {
			return 10;
		}
		return Integer.parseInt(preference.getString("HistorySize", "10"));
	}

	private static String getWebColor(String color) {
		if (color.length() > 7) {
			int lenght = color.length();
			return "#" + color.substring(lenght - 6);
		} else {
			return color;
		}
	}

	public static boolean crossRefViewDetails() {
        return preference != null && preference.getBoolean("cross_reference_display_context", false);
    }

	public static boolean volumeButtonsToScroll() {
        return preference != null && preference.getBoolean("volume_butons_to_scroll", false);
    }

	public static boolean textAlignJustify() {
        return preference != null && preference.getBoolean("text_align_justify", false);
    }

	public static String getFontFamily() {
		if (preference == null) {
			return "sans-serif";
		}
		return preference.getString("font_family", "sans-serif");
	}

	public static boolean divideTheVerses() {
        return preference != null && preference.getBoolean("divide_the_verses", false);
    }

	public static boolean addReference() {
        return preference == null || preference.getBoolean("add_reference", true);
    }

	public static boolean putReferenceInBeginning() {
        return preference != null && preference.getBoolean("put_reference_in_beginning", false);
    }

	public static boolean shortReference() {
        return preference != null && preference.getBoolean("short_reference", false);
    }

	public static boolean addModuleToBibleReference() {
        return preference == null || preference.getBoolean("add_module_to_reference", true);
    }
}
