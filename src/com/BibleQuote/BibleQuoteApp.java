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
package com.BibleQuote;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.managers.bookmarks.repository.IBookmarksRepository;
import com.BibleQuote.managers.bookmarks.repository.dbBookmarksRepository;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.UpdateManager;

public class BibleQuoteApp extends Application {

	private static final String TAG = "BibleQuoteApp";

	private Librarian myLibrarian;
	private AsyncManager mAsyncManager;

	public static String getAppVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return "0.00.01";
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public void Init() {
		Log.i(TAG, "Init application preference helper...");
		initPrefernceHelper();
		Log.i(TAG, "Start update manager...");
		UpdateManager.Init(this);
		if (myLibrarian == null) {
			Log.i(TAG, "Init library...");
			initLibrarian();
		}
	}

	public void RestartInit() {
		Log.i(TAG, "Init application preference helper...");
		initPrefernceHelper();
		Log.i(TAG, "Init library...");
		initLibrarian();
	}

	public Librarian getLibrarian() {
		if (myLibrarian == null) {
			// Сборщик мусора уничтожил ссылки на myLibrarian и на PreferenceHelper
			// Восстановим ссылки
			initPrefernceHelper();
			initLibrarian();
		}
		return myLibrarian;
	}

	public AsyncManager getAsyncManager() {
		if (mAsyncManager == null) {
			mAsyncManager = new AsyncManager();
		}
		return mAsyncManager;
	}

	public IBookmarksRepository getBookmarksRepository() {
		//return new prefBookmarksRepository();
		return new dbBookmarksRepository();
	}

	private void initPrefernceHelper() {
		PreferenceHelper.Init(this);
	}

	private void initLibrarian() {
		myLibrarian = new Librarian(this);
	}

	public boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (BibleQuoteService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
