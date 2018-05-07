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
 * File: DataModule.java
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.di.module;

import android.content.Context;

import com.BibleQuote.dal.DbLibraryHelper;
import com.BibleQuote.dal.repository.FsHistoryRepository;
import com.BibleQuote.dal.repository.XmlTskRepository;
import com.BibleQuote.dal.repository.bookmarks.DbBookmarksRepository;
import com.BibleQuote.dal.repository.bookmarks.DbTagsRepository;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.domain.repository.IHistoryRepository;
import com.BibleQuote.domain.repository.ITagsRepository;
import com.BibleQuote.domain.repository.ITskRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    IBookmarksRepository getBookmarksRepository(DbLibraryHelper dbLibraryHelper) {
        return new DbBookmarksRepository(dbLibraryHelper);
    }

    @Provides
    @Singleton
    ITagsRepository getBookmarksTagsRepository(DbLibraryHelper dbLibraryHelper) {
        return new DbTagsRepository(dbLibraryHelper);
    }

    @Provides
    @Singleton
    DbLibraryHelper getDbLibraryHelper(Context context) {
        return new DbLibraryHelper(context);
    }

    @Provides
    IHistoryRepository getHistoryRepository(Context context) {
        return new FsHistoryRepository(context);
    }

    @Provides
    ITskRepository getTskRepository(Context context) {
        return new XmlTskRepository(context.getFilesDir());
    }

}
