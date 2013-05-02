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

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.AsyncCommand;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.async.command.InitApplication;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;
import com.actionbarsherlock.app.SherlockActivity;

public class SplashActivity extends SherlockActivity implements OnTaskCompleteListener {

	private static final String TAG = "SplashActivity";
	private AsyncCommand initApp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		Log.Init(getApplicationContext());

		BibleQuoteApp app = (BibleQuoteApp) getApplication();

		AsyncManager myAsyncManager = app.getAsyncManager();
		if (initApp != null) {
			Log.i(TAG, "Restore old task...");
			myAsyncManager.handleRetainedTask(initApp, this);
		} else {
			Log.i(TAG, "Start task InitApplication...");
			myAsyncManager.setupTask(getTaskObject(), this);
		}
	}

	private AsyncCommand getTaskObject() {
		String progressMessage = getResources().getString(R.string.messageLoad);
		initApp = new AsyncCommand(new InitApplication(this), progressMessage, true);
		return initApp;
	}

	@Override
	public void onTaskComplete(Task task) {
		Log.i(TAG, "Start reader activity");
		startActivity(new Intent(this, ReaderActivity.class));
		finish();
	}
}