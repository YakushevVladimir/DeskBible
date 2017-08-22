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
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote;

import android.app.Application;

import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.di.AppComponent;
import com.BibleQuote.di.AppModule;
import com.BibleQuote.di.DaggerAppComponent;
import com.BibleQuote.domain.controllers.ILibraryController;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.PreferenceHelper;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

public class BibleQuoteApp extends Application {

	private static BibleQuoteApp instance;

    @Inject AsyncManager asyncManager;
    @Inject IBookmarksRepository bookmarksRepository;
    @Inject Librarian librarian;
    @Inject ILibraryController libraryController;
    @Inject PreferenceHelper prefHelper;

	public static BibleQuoteApp getInstance() {
		return instance;
	}

    public BibleQuoteApp() {
        super();
        instance = this;
    }

    public AsyncManager getAsyncManager() {
        return asyncManager;
    }

    public IBookmarksRepository getBookmarksRepository() {
        return bookmarksRepository;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public ILibraryController getLibraryController() {
        return libraryController;
    }

    public PreferenceHelper getPrefHelper() {
        return prefHelper;
    }

	public synchronized Tracker getTracker() {
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		Tracker tracker = analytics.newTracker(R.xml.analitics);
		tracker.enableAdvertisingIdCollection(true);
		return tracker;
	}

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
    }
}
