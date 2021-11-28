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

import com.BibleQuote.BuildConfig;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.dal.controller.CachedLibraryRepository;
import com.BibleQuote.dal.controller.FsLibraryController;
import com.BibleQuote.dal.controller.TSKController;
import com.BibleQuote.dal.repository.BQModuleRepository;
import com.BibleQuote.dal.repository.FsCacheRepository;
import com.BibleQuote.dal.repository.FsLibraryLoader;
import com.BibleQuote.domain.AnalyticsHelper;
import com.BibleQuote.domain.analytics.FirebaseAnalyticsHelper;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.controller.ITSKController;
import com.BibleQuote.domain.repository.ICacheRepository;
import com.BibleQuote.domain.repository.IHistoryRepository;
import com.BibleQuote.domain.repository.ITskRepository;
import com.BibleQuote.domain.repository.LibraryLoader;
import com.BibleQuote.managers.history.HistoryManager;
import com.BibleQuote.managers.history.IHistoryManager;
import com.BibleQuote.utils.FsUtilsWrapper;
import com.BibleQuote.utils.PreferenceHelper;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.churchtools.deskbible.data.library.LibraryContext;
import ru.churchtools.deskbible.data.logger.AndroidLogger;
import ru.churchtools.deskbible.data.logger.CrashlyticsLogger;
import ru.churchtools.deskbible.domain.logger.CompositeLogger;
import ru.churchtools.deskbible.domain.logger.Logger;
import ru.churchtools.deskbible.domain.migration.Migration;
import ru.churchtools.deskbible.domain.migration.UpdateManager;

@Module
public class AppModule {

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
    ILibraryController getLibraryController(LibraryContext context, LibraryLoader repository) {
        ICacheRepository cacheRepository = new FsCacheRepository(context.libraryCacheFile());
        return new FsLibraryController(repository, new CachedLibraryRepository(cacheRepository));
    }

    @Provides
    LibraryLoader getLibraryLoader(LibraryContext libraryContext) {
        List<File> modulesDir = Arrays.asList(
                libraryContext.modulesDir(),
                libraryContext.modulesExternalDir());
        final FsUtilsWrapper fsUtils = new FsUtilsWrapper();
        return new FsLibraryLoader(modulesDir, new BQModuleRepository(fsUtils));
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
    AnalyticsHelper analyticsHelper(Context context) {
        return new FirebaseAnalyticsHelper(FirebaseAnalytics.getInstance(context.getApplicationContext()));
    }

    @Provides
    @Singleton
    Logger provideLogger() {
        final List<Logger> loggers = new ArrayList<>();
       loggers.add(new CrashlyticsLogger(FirebaseCrashlytics.getInstance()));
        if (BuildConfig.DEBUG) {
            loggers.add(new AndroidLogger());
        }

        return new CompositeLogger(loggers);
    }

    @Singleton
    @Provides
    UpdateManager provideUpdateManager(PreferenceHelper prefHelper, Set<Migration> migrations) {
        return new UpdateManager(prefHelper, migrations);
    }
}
