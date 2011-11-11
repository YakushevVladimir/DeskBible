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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

public class BibleQuote extends GDActivity {

    private static final int START_HOME_ACTIVITY = 10;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setActionBarContentView(R.layout.main);

		getActionBar().setVisibility(View.GONE);

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
					app.Init();
				} finally {
					Message msg = new Message();
					msg.what = START_HOME_ACTIVITY;
					splashHandler.sendMessageDelayed(msg, 100);
				}
			}
		};
		splashTread.start();
	}
    
    public void startHomeActivity(){
		startActivity(new Intent(this, Reader.class));
		finish();;
    }

	private Handler splashHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_HOME_ACTIVITY:
				startHomeActivity();
				break;
			}
			super.handleMessage(msg);
		}
	};
}