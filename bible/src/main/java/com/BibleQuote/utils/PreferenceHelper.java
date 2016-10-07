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
 * Created by Vladimir Yakushev at 10/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.BibleQuote.entity.TextAppearance;

public final class PreferenceHelper {

    private static final String KEY_NIGHT_MODE = "nightMode";
    private static final String KEY_VIEW_BOOK_VERSE = "always_view_verse_numbers";
    private static final String KEY_HISTORY_SIZE = "HistorySize";
    private static final String KEY_READ_MODE_BY_DEFAULT = "ReadModeByDefault";
    private static final String KEY_ADD_MODULE_TO_REFERENCE = "add_module_to_reference";
    private static final String KEY_CROSS_REFERENCE_DISPLAY_CONTEXT = "cross_reference_display_context";
    private static final String KEY_DIVIDE_THE_VERSES = "divide_the_verses";
    private static final String KEY_HIDE_NAV_BUTTONS = "hide_nav_buttons";
    private static final String KEY_PUT_REFERENCE_IN_BEGINNING = "put_reference_in_beginning";
    private static final String KEY_SHORT_REFERENCE = "short_reference";
    private static final String KEY_VOLUME_BUTTONS_TO_SCROLL = "volume_butons_to_scroll";
    private static final String KEY_FONT_FAMILY = "font_family";
    private static final String KEY_TEXT_BG = "TextBG";
    private static final String KEY_TEXT_BG_SEL = "TextBGSel";
    private static final String KEY_TEXT_COLOR = "TextColor";
    private static final String KEY_TEXT_COLOR_SEL = "TextColorSel";
    private static final String KEY_TEXT_SIZE = "TextSize";
    private static final String KEY_TEXT_ALIGN_JUSTIFY = "text_align_justify";
    private static final String KEY_ADD_REFERENCE = "add_reference";

    private static final String DEF_TYPEFACE = "sans-serif";
    private static final String DEF_HISTORY_SIZE = "50";
    private static final String DEF_TEXT_BG = "#ffffff";
    private static final String DEF_TEXT_BG_SEL = "#FEF8C4";
    private static final String DEF_TEXT_COLOR = "#000000";
    private static final String DEF_TEXT_COLOR_SEL = "#000000";
    private static final int DEF_TEXT_SIZE = 12;

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
                    "PreferenceHelper.createInstance() needs to be called "
                            + "before PreferenceHelper.getInstance()");
        }
        return instance;
    }

    public Integer getHistorySize() {
        return Integer.parseInt(preference.getString(KEY_HISTORY_SIZE, DEF_HISTORY_SIZE));
    }

    public TextAppearance getTextAppearance() {
        return new TextAppearance(
                getFontFamily(), getTextSize(), getTextColor(), getTextBackground(),
                getTextColorSelected(), getTextBackgroundSelected(),
                textAlignJustify() ? "justify" : "left", getNightMode()
        );
    }

    public boolean isReadModeByDefault() {
        return preference.getBoolean(KEY_READ_MODE_BY_DEFAULT, false);
    }

    public void setNightMode(boolean nightMode) {
        preference.edit().putBoolean(KEY_NIGHT_MODE, nightMode).apply();
    }

    public boolean addModuleToBibleReference() {
        return preference.getBoolean(KEY_ADD_MODULE_TO_REFERENCE, true);
    }

    public boolean addReference() {
        return preference.getBoolean(KEY_ADD_REFERENCE, true);
    }

    public boolean crossRefViewDetails() {
        return preference.getBoolean(KEY_CROSS_REFERENCE_DISPLAY_CONTEXT, false);
    }

    public boolean divideTheVerses() {
        return preference.getBoolean(KEY_DIVIDE_THE_VERSES, false);
    }

    @NonNull
    public Boolean getBoolean(String key) {
        return preference.getBoolean(key, false);
    }

    public int getInt(String key) {
        return preference.getInt(key, 0);
    }

    @NonNull
    public String getString(String key) {
        return preference.getString(key, "");
    }

    public boolean hideNavButtons() {
        return preference.getBoolean(KEY_HIDE_NAV_BUTTONS, false);
    }

    public boolean putReferenceInBeginning() {
        return preference.getBoolean(KEY_PUT_REFERENCE_IN_BEGINNING, false);
    }

    public void saveInt(String key, int value) {
        preference.edit().putInt(key, value).apply();
    }

    public void saveString(String key, String value) {
        preference.edit().putString(key, value).apply();
    }

    public boolean shortReference() {
        return preference.getBoolean(KEY_SHORT_REFERENCE, false);
    }

    public boolean viewBookVerse() {
        return preference.getBoolean(PreferenceHelper.KEY_VIEW_BOOK_VERSE, false);
    }

    public boolean volumeButtonsToScroll() {
        return preference.getBoolean(KEY_VOLUME_BUTTONS_TO_SCROLL, false);
    }

    private String getFontFamily() {
        return preference.getString(KEY_FONT_FAMILY, DEF_TYPEFACE);
    }

    private boolean getNightMode() {
        return getBoolean(KEY_NIGHT_MODE);
    }

    private String getTextBackground() {
        return getWebColor(preference.getString(KEY_TEXT_BG, DEF_TEXT_BG));
    }

    private String getTextBackgroundSelected() {
        return getWebColor(preference.getString(KEY_TEXT_BG_SEL, DEF_TEXT_BG_SEL));
    }

    private String getTextColor() {
        return getWebColor(preference.getString(KEY_TEXT_COLOR, DEF_TEXT_COLOR));
    }

    private String getTextColorSelected() {
        return getWebColor(preference.getString(KEY_TEXT_COLOR_SEL, DEF_TEXT_COLOR_SEL));
    }

    private String getTextSize() {
        return String.valueOf(preference.getInt(KEY_TEXT_SIZE, DEF_TEXT_SIZE));
    }

    private String getWebColor(String color) {
        if (color.length() > 7) {
            int lenght = color.length();
            return "#" + color.substring(lenght - 6);
        } else {
            return color;
        }
    }

    private boolean textAlignJustify() {
        return preference.getBoolean(KEY_TEXT_ALIGN_JUSTIFY, false);
    }
}
