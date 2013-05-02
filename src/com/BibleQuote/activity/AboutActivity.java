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
package com.BibleQuote.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.utils.ViewUtils;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AboutActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		ViewUtils.setActionBarBackground(this);

		TextView tv_app = (TextView) findViewById(R.id.about_name);
		tv_app.setText(String.format(
				getResources().getText(R.string.app_about_name).toString(),
				BibleQuoteApp.getAppVersionName(getApplicationContext())));
	}
}
