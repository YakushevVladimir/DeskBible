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

import greendroid.app.GDActivity;

import com.BibleQuote.R;
import com.BibleQuote.BibleQuoteApp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class BibleQuote extends GDActivity {

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setActionBarContentView(R.layout.main);

		getActionBar().setVisibility(View.GONE);

		new InitApplication().execute(true);
	}
    
    public void startHomeActivity(){
		startActivity(new Intent(this, Reader.class));
		finish();;
    }

	private class InitApplication extends AsyncTask<Boolean, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
			startHomeActivity();
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(Boolean... params) {
			BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
			app.Init();
			return true;
		}
	}
}