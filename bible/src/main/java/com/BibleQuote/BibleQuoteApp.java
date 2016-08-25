/*
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
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: BibleQuoteApp.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */
package com.BibleQuote;

import android.app.Application;

import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.dal.repository.bookmarks.dbBookmarksRepository;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.UpdateManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class BibleQuoteApp extends Application {

	private static final String TAG = "BibleQuoteApp";
	private static BibleQuoteApp instance;

	private Librarian myLibrarian;
	private AsyncManager mAsyncManager;

	public static BibleQuoteApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public void init() {
		Log.i(TAG, "Init application preference helper...");
		initPreferenceHelper();
		Log.i(TAG, "Start update manager...");
		UpdateManager.Init(this);
		if (myLibrarian == null) {
			Log.i(TAG, "Init library...");
			initLibrarian();
		}
	}

	public Librarian getLibrarian() {
		if (myLibrarian == null) {
			// Сборщик мусора уничтожил ссылки на myLibrarian и на PreferenceHelper
			// Восстановим ссылки
			initPreferenceHelper();
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
		return new dbBookmarksRepository();
	}

	private void initPreferenceHelper() {
		PreferenceHelper.Init(this);
	}

	private void initLibrarian() {
		myLibrarian = new Librarian(this);
	}

	public synchronized Tracker getTracker() {
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		Tracker tracker = analytics.newTracker(R.xml.analitics);
		tracker.enableAdvertisingIdCollection(true);
		return tracker;
	}
}
