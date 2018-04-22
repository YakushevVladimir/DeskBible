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
 * File: AppModule.java
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.di.module;

import android.content.Context;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.BuildConfig;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.dal.controller.CachedLibraryRepository;
import com.BibleQuote.dal.controller.FsLibraryController;
import com.BibleQuote.dal.controller.TSKController;
import com.BibleQuote.dal.repository.FsCacheRepository;
import com.BibleQuote.dal.repository.FsLibraryLoader;
import com.BibleQuote.data.analytics.GoogleAnalyticsHelper;
import com.BibleQuote.data.logger.AndroidLogger;
import com.BibleQuote.data.logger.FileLogger;
import com.BibleQuote.domain.AnalyticsHelper;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.controller.ITSKController;
import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.logger.CompositeLogger;
import com.BibleQuote.domain.logger.Logger;
import com.BibleQuote.domain.repository.ICacheRepository;
import com.BibleQuote.domain.repository.IHistoryRepository;
import com.BibleQuote.domain.repository.ITskRepository;
import com.BibleQuote.domain.repository.LibraryLoader;
import com.BibleQuote.managers.history.HistoryManager;
import com.BibleQuote.managers.history.IHistoryManager;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.FsUtilsWrapper;
import com.BibleQuote.utils.PreferenceHelper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {DataModule.class})
public class AppModule {

    private BibleQuoteApp application;

    public AppModule(BibleQuoteApp application) {
        this.application = application;
    }

    @Provides
    Context getAppContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    AsyncManager getAsyncManager() {
        return new AsyncManager();
    }

    @Provides
    IHistoryManager getHistoryManager(IHistoryRepository repository, PreferenceHelper prefHelper) {
        return new HistoryManager(repository, prefHelper.getHistorySize());
    }

    @Provides
    @Singleton
    ILibraryController getLibraryController(Context context, LibraryLoader<? extends BaseModule> repository) {
        ICacheRepository cacheRepository = new FsCacheRepository(context.getFilesDir(), DataConstants.getLibraryCache());
        return new FsLibraryController(repository, new CachedLibraryRepository(cacheRepository));
    }

    @Provides
    LibraryLoader<? extends BaseModule> getLibraryLoader(Context context) {
        List<File> libraryDirs = Arrays.asList(
                new File(context.getFilesDir(), BuildConfig.MODULE_DIR_NAME),
                new File(DataConstants.getLibraryPath()));
        return new FsLibraryLoader(new FsUtilsWrapper(), libraryDirs);
    }

    @Provides
    PreferenceHelper getPreferenceHelper(Context context) {
        return new PreferenceHelper(context);
    }

    @Provides
    ITSKController getTskController(ITskRepository repository) {
        return new TSKController(repository);
    }

    @Provides
    AnalyticsHelper analyticsHelper() {
        return new GoogleAnalyticsHelper(application.getTracker());
    }

    @Provides
    @Singleton Logger provideLogger() {
        return new CompositeLogger(Arrays.asList(new AndroidLogger(), new FileLogger()));
    }
}
