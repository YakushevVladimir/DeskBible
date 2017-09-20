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
 * File: SettingsFragment.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.ui.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.utils.PreferenceHelper;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        PreferenceHelper prefHelper = BibleQuoteApp.getInstance().getPrefHelper();

        Preference historySize = findPreference("HistorySize");
        historySize.setOnPreferenceChangeListener((preference1, newValue1) -> {
            setHistorySummary(preference1, (String) newValue1);
            return true;
        });
        setHistorySummary(historySize, Integer.toString(prefHelper.getHistorySize()));

        Preference fontFamily = findPreference("font_family");
        fontFamily.setOnPreferenceChangeListener((preference, newValue) -> {
            setFontFamilySummary(preference, (String) newValue);
            return true;
        });
        setFontFamilySummary(fontFamily, prefHelper.getTextAppearance().getTypeface());
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

    private void setHistorySummary(Preference historySize, String value) {
        try {
            String summary = getResources().getString(R.string.category_reader_other_history_size_summary);
            historySize.setSummary(String.format(summary, value));
        } catch (NumberFormatException | Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
