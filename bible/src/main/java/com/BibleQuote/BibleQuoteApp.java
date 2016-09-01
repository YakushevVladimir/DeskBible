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
 * File: BibleQuoteApp.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote;

import android.app.Application;

import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.dal.repository.FsHistoryRepository;
import com.BibleQuote.dal.repository.XmlTskRepository;
import com.BibleQuote.dal.repository.bookmarks.DB_BookmarksRepository;
import com.BibleQuote.domain.controllers.FsLibraryController;
import com.BibleQuote.domain.controllers.ILibraryController;
import com.BibleQuote.domain.controllers.TSKController;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.managers.history.HistoryManager;
import com.BibleQuote.utils.PreferenceHelper;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class BibleQuoteApp extends Application {

	private static BibleQuoteApp instance;

	private Librarian myLibrarian;
	private AsyncManager mAsyncManager;
    private ILibraryController libraryController;

	public static BibleQuoteApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
        PreferenceHelper.createInstance(this);
    }

    public ILibraryController getLibraryController() {
        if (libraryController == null) {
            libraryController = FsLibraryController.getInstance(this);
            libraryController.getModules();
        }
        return libraryController;
    }

    public Librarian getLibrarian() {
        if (myLibrarian == null) {
            myLibrarian = new Librarian(
                    getLibraryController(),
                    new TSKController(new XmlTskRepository()),
                    new HistoryManager(new FsHistoryRepository(this),
                            PreferenceHelper.getInstance().getHistorySize()));
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
        return new DB_BookmarksRepository();
    }

	public synchronized Tracker getTracker() {
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		Tracker tracker = analytics.newTracker(R.xml.analitics);
		tracker.enableAdvertisingIdCollection(true);
		return tracker;
	}
}
