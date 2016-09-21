/*
 * Copyright (C) 2011 Scripture Software
 *
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
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public final class PreferenceHelper {

    private static final String KEY_VIEW_BOOK_VERSE = "always_view_verse_numbers";
    private static volatile PreferenceHelper instance;

    private final SharedPreferences preference;

    private PreferenceHelper(Context context) {
        preference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceHelper createInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    public static PreferenceHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "PreferenceHelper::createInstance() needs to be called "
                            + "before PreferenceHelper::getInstance()");
        }
        return instance;
    }

    public String getFontFamily() {
        return preference.getString("font_family", "sans-serif");
    }

    public Integer getHistorySize() {
        return Integer.parseInt(preference.getString("HistorySize", "50"));
    }

    public String getTextBackground() {
        return getWebColor(preference.getString("TextBG", "#ffffff"));
    }

    public String getTextBackgroundSelected() {
        return getWebColor(preference.getString("TextBGSel", "#FEF8C4"));
    }

    public String getTextColor() {
        return getWebColor(preference.getString("TextColor", "#000000"));
    }

    public String getTextColorSelected() {
        return getWebColor(preference.getString("TextColorSel", "#000000"));
    }

    public String getTextSize() {
        return String.valueOf(preference.getInt("TextSize", 12));
	}

    public boolean isReadModeByDefault() {
        return preference.getBoolean("ReadModeByDefault", false);
    }

    public boolean addModuleToBibleReference() {
        return preference.getBoolean("add_module_to_reference", true);
    }

    public boolean addReference() {
        return preference.getBoolean("add_reference", true);
    }

    public boolean crossRefViewDetails() {
        return preference.getBoolean("cross_reference_display_context", false);
    }

    public boolean divideTheVerses() {
        return preference.getBoolean("divide_the_verses", false);
    }

    public boolean hideNavButtons() {
        return preference.getBoolean("hide_nav_buttons", false);
    }

    public boolean putReferenceInBeginning() {
        return preference.getBoolean("put_reference_in_beginning", false);
    }

    @NonNull
    public Boolean restoreStateBoolean(String key) {
        return preference.getBoolean(key, false);
    }

    public int restoreStateInt(String key) {
        return preference.getInt(key, 0);
    }

    @NonNull
    public String restoreStateString(String key) {
        return preference.getString(key, "");
    }

    public void saveStateBoolean(String key, Boolean v) {
        preference.edit().putBoolean(key, v).apply();
    }

    public void saveStateInt(String key, int value) {
        preference.edit().putInt(key, value).apply();
    }

    public void saveStateString(String key, String value) {
        preference.edit().putString(key, value).apply();
    }

    public boolean shortReference() {
        return preference.getBoolean("short_reference", false);
    }

    public boolean textAlignJustify() {
        return preference.getBoolean("text_align_justify", false);
    }

    public boolean viewBookVerse() {
        return preference.getBoolean(PreferenceHelper.KEY_VIEW_BOOK_VERSE, false);
    }

    public boolean volumeButtonsToScroll() {
        return preference.getBoolean("volume_butons_to_scroll", false);
    }

    private String getWebColor(String color) {
        if (color.length() > 7) {
            int lenght = color.length();
            return "#" + color.substring(lenght - 6);
        } else {
            return color;
        }
    }
}
