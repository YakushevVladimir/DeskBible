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
 * File: ActivityComponent.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.di.component;

import com.BibleQuote.di.module.ActivityModule;
import com.BibleQuote.di.scope.PerActivity;
import com.BibleQuote.presentation.ui.bookmarks.BookmarksActivity;
import com.BibleQuote.presentation.ui.crossreference.CrossReferenceActivity;
import com.BibleQuote.presentation.ui.help.HelpActivity;
import com.BibleQuote.presentation.ui.history.HistoryActivity;
import com.BibleQuote.presentation.ui.imagepreview.ImagePreviewActivity;
import com.BibleQuote.presentation.ui.library.LibraryActivity;
import com.BibleQuote.presentation.ui.reader.ReaderActivity;
import com.BibleQuote.presentation.ui.search.SearchActivity;
import com.BibleQuote.presentation.ui.settings.SettingsActivity;

import dagger.Subcomponent;
import ru.churchtools.deskbible.presentation.splash.SplashActivity;

@PerActivity
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {

    void inject(ImagePreviewActivity activity);
    void inject(ReaderActivity activity);
    void inject(LibraryActivity activity);
    void inject(SplashActivity activity);
    void inject(SettingsActivity activity);
    void inject(SearchActivity activity);
    void inject(HistoryActivity activity);
    void inject(HelpActivity activity);
    void inject(CrossReferenceActivity activity);
    void inject(BookmarksActivity activity);
}
