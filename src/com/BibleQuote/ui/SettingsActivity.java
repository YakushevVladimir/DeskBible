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
package com.BibleQuote.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import com.BibleQuote.R;
import com.BibleQuote.utils.PreferenceHelper;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		Preference historySize = (Preference) findPreference("HistorySize");
		historySize.setOnPreferenceChangeListener(historySizeChangeListener);
		setHistorySummary(historySize, Integer.toString(PreferenceHelper.getHistorySize()));

		Preference fontFamily = (Preference) findPreference("font_family");
		fontFamily.setOnPreferenceChangeListener(fontFamilyChangeListener);
		setFontFamilySummary(fontFamily, PreferenceHelper.getFontFamily());
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDestroy() {
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	private OnPreferenceChangeListener historySizeChangeListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			setHistorySummary(preference, (String) newValue);
			return true;
		}
	};

	private void setHistorySummary(Preference historySize, String value) {
		try {
			String summary = getResources().getString(R.string.category_reader_other_history_size_summary);
			historySize.setSummary(String.format(summary, value));
		} catch (NumberFormatException e) {
			return;
		} catch (NotFoundException e) {
			return;
		}
	}

	private OnPreferenceChangeListener fontFamilyChangeListener = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			setFontFamilySummary(preference, (String) newValue);
			return true;
		}
	};


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