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
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.BibleQuote.R;
import com.BibleQuote.entity.TextAppearance;

public class PreferenceHelper {

    private static final String DEF_HISTORY_SIZE = "50";
    private static final int DEF_LINE_SPACING = 150;
    private static final int DEF_TEXT_SIZE = 13;
    private static final String DEF_TYPEFACE = "serif";

    private static final String KEY_ADD_MODULE_TO_REFERENCE = "add_module_to_reference";
    private static final String KEY_ADD_REFERENCE = "add_reference";
    private static final String KEY_CROSS_REFERENCE_DISPLAY_CONTEXT = "cross_reference_display_context";
    private static final String KEY_DIVIDE_THE_VERSES = "divide_the_verses";
    private static final String KEY_FONT_FAMILY = "font_family";
    private static final String KEY_HIDE_NAV_BUTTONS = "hide_nav_buttons";
    private static final String KEY_HISTORY_SIZE = "HistorySize";
    private static final String KEY_LINE_SPACING = "line_spacing";
    private static final String KEY_NIGHT_MODE = "nightMode";
    private static final String KEY_PUT_REFERENCE_IN_BEGINNING = "put_reference_in_beginning";
    private static final String KEY_READ_MODE_BY_DEFAULT = "ReadModeByDefault";
    private static final String KEY_SHORT_REFERENCE = "short_reference";
    private static final String KEY_TEXT_ALIGN_JUSTIFY = "text_align_justify";
    private static final String KEY_TEXT_BG = "background";
    private static final String KEY_TEXT_BG_SEL = "sel_background";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_TEXT_COLOR_SEL = "sel_text_color";
    private static final String KEY_TEXT_SIZE = "TextSize";
    private static final String KEY_VIEW_BOOK_VERSE = "always_view_verse_numbers";
    private static final String KEY_VOLUME_BUTTONS_TO_SCROLL = "volume_butons_to_scroll";
    private static final String KEY_LAST_READ = "last_read";

    private final SharedPreferences preference;
    private Context context;

    public PreferenceHelper(Context context) {
        preference = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context.getApplicationContext();
    }

    public Integer getHistorySize() {
        return Integer.parseInt(preference.getString(KEY_HISTORY_SIZE, DEF_HISTORY_SIZE));
    }

    public TextAppearance getTextAppearance() {
        return TextAppearance.builder()
                .typeface(getFontFamily())
                .textSize(getTextSize())
                .textColor(getTextColor())
                .background(getTextBackground())
                .selectedTextColor(getTextColorSelected())
                .selectedBackgroung(getTextBackgroundSelected())
                .textAlign(textAlignJustify() ? "justify" : "left")
                .nightMode(getNightMode())
                .lineSpacing(getLineSpacing())
                .build();
    }

    public String getLastRead() {
        return preference.getString(KEY_LAST_READ, "");
    }

    public boolean isReadModeByDefault() {
        return preference.getBoolean(KEY_READ_MODE_BY_DEFAULT, false);
    }

    public boolean addModuleToBibleReference() {
        return preference.getBoolean(KEY_ADD_MODULE_TO_REFERENCE, true);
    }

    public boolean addReference() {
        return preference.getBoolean(KEY_ADD_REFERENCE, true);
    }

    public boolean crossRefViewDetails() {
        return preference.getBoolean(KEY_CROSS_REFERENCE_DISPLAY_CONTEXT, true);
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
        return preference.getBoolean(KEY_HIDE_NAV_BUTTONS, true);
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

    private int getLineSpacing() {
        return preference.getInt(KEY_LINE_SPACING, DEF_LINE_SPACING);
    }

    private boolean getNightMode() {
        return getBoolean(KEY_NIGHT_MODE);
    }

    public void setNightMode(boolean nightMode) {
        preference.edit().putBoolean(KEY_NIGHT_MODE, nightMode).apply();
    }

    @SuppressWarnings("deprecation")
    private String getTextBackground() {
        int color = preference.getInt(KEY_TEXT_BG, context.getResources().getColor(R.color.def_background));
        return ColorUtils.toWeb(color);
    }

    @SuppressWarnings("deprecation")
    private String getTextBackgroundSelected() {
        int color = preference.getInt(KEY_TEXT_BG_SEL, context.getResources().getColor(R.color.def_sel_background));
        return ColorUtils.toWeb(color);
    }

    @SuppressWarnings("deprecation")
    private String getTextColor() {
        int color = preference.getInt(KEY_TEXT_COLOR, context.getResources().getColor(R.color.def_text_color));
        return ColorUtils.toWeb(color);
    }

    @SuppressWarnings("deprecation")
    private String getTextColorSelected() {
        int color = preference.getInt(KEY_TEXT_COLOR_SEL, context.getResources().getColor(R.color.def_sel_text_color));
        return ColorUtils.toWeb(color);
    }

    private String getTextSize() {
        return String.valueOf(preference.getInt(KEY_TEXT_SIZE, DEF_TEXT_SIZE));
    }

    private boolean textAlignJustify() {
        return preference.getBoolean(KEY_TEXT_ALIGN_JUSTIFY, false);
    }
}
