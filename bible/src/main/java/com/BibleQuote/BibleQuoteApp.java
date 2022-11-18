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
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.PreferenceHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import ru.churchtools.deskbible.di.app.AppComponent;
import ru.churchtools.deskbible.di.app.DaggerAppComponent;
import ru.churchtools.deskbible.domain.logger.Logger;
import ru.churchtools.deskbible.domain.logger.StaticLogger;

public class BibleQuoteApp extends Application implements Thread.UncaughtExceptionHandler {

    private static BibleQuoteApp instance;

    @Inject AsyncManager asyncManager;
    @Inject IBookmarksRepository bookmarksRepository;
    @Inject Librarian librarian;
    @Inject ILibraryController libraryController;
    @Inject PreferenceHelper prefHelper;
    @Inject Logger logger;

    private AppComponent appComponent;

    private final Thread.UncaughtExceptionHandler exceptionHandler;

    public BibleQuoteApp() {
        super();
        instance = this;
        exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static BibleQuoteApp getInstance() {
        return instance;
    }

    public static BibleQuoteApp instance(Context context) {
        return (BibleQuoteApp) context.getApplicationContext();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public AsyncManager getAsyncManager() {
        return asyncManager;
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

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalytics.getInstance(this);

        appComponent = DaggerAppComponent.factory().create(this);
        appComponent.inject(this);

        StaticLogger.init(logger);
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, @NonNull Throwable ex) {
        logger.error(thread.getName(), Log.getStackTraceString(ex));
        if (exceptionHandler != null) {
            exceptionHandler.uncaughtException(thread, ex);
        }
    }
}
