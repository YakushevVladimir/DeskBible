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
 * File: SettingsActivity.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.BibleQuote.R;
import com.BibleQuote.utils.PreferenceHelper;

public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    private OnPreferenceChangeListener historySizeChangeListener = new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            setHistorySummary(preference, (String) newValue);
            return true;
        }
    };
    private OnPreferenceChangeListener fontFamilyChangeListener = new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            setFontFamilySummary(preference, (String) newValue);
            return true;
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference historySize = findPreference("HistorySize");
        historySize.setOnPreferenceChangeListener(historySizeChangeListener);
        setHistorySummary(historySize, Integer.toString(PreferenceHelper.getInstance().getHistorySize()));

        Preference fontFamily = findPreference("font_family");
        fontFamily.setOnPreferenceChangeListener(fontFamilyChangeListener);
        setFontFamilySummary(fontFamily, PreferenceHelper.getInstance().getFontFamily());
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private void setHistorySummary(Preference historySize, String value) {
        try {
            String summary = getResources().getString(R.string.category_reader_other_history_size_summary);
            historySize.setSummary(String.format(summary, value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setFontFamilySummary(Preference fontFamily, String newValue) {
        String summary;
        if (newValue.equalsIgnoreCase("serif")) {
            summary = "Droid Serif";
        } else if (newValue.equalsIgnoreCase("monospace")) {
            summary = "Droid Sans Mono";
        } else {
            summary = "Droid Sans";
        }

        fontFamily.setSummary(summary);
    }
}