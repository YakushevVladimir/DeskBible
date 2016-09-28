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
 * File: GoogleAnalyticsHelper.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.managers;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;
import com.google.android.gms.analytics.HitBuilders;

/**
 * @author Vladimir Yakushev
 * @version 1.0
 */
public final class GoogleAnalyticsHelper {

    private static final String CATEGORY_BOOKMARKS = "bookmarks";
    private static final String CATEGORY_MODULES = "modules";
    private static final String CATEGORY_SEARCH = "search";

    private static final String ACTION_OPEN_MODULE = "open_module";
    private static final String ACTION_OPEN_BOOK = "open_book";

    private static volatile GoogleAnalyticsHelper instance;

    private GoogleAnalyticsHelper() {
    }

    public static GoogleAnalyticsHelper getInstance() {
        if (instance == null) {
            synchronized (GoogleAnalyticsHelper.class) {
                if (instance == null) {
                    instance = new GoogleAnalyticsHelper();
                }
            }
        }
        return instance;
    }

    public void actionOpenLink(BibleReference reference) {
        createEvent(CATEGORY_MODULES, ACTION_OPEN_MODULE, reference.getModuleID());
        createEvent(CATEGORY_MODULES, ACTION_OPEN_BOOK, reference.getBookID());
    }

    public void actionSearch(String moduleID, String query) {
        createEvent(CATEGORY_SEARCH, moduleID, query);
    }

    public void actionSendBookmark(Bookmark bookmark) {
        for(String tag : bookmark.tags.split(",")) {
            if (!"".equals(tag)) {
                createEvent(CATEGORY_BOOKMARKS, "tags", tag, bookmark.OSISLink);
            }
        }
        createEvent(CATEGORY_BOOKMARKS, "bookmark", bookmark.OSISLink);
    }

    private void createEvent(String category, String action, String label) {
        BibleQuoteApp.getInstance().getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    private void createEvent(String category, String action, String label, String dimension1) {
        BibleQuoteApp.getInstance().getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setCustomDimension(1, dimension1)
                .build());
    }
}
