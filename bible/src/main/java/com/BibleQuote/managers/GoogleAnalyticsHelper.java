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
 * Project: BibleQuote-for-Android
 * File: GoogleAnalyticsHelper.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 *
 */

package com.BibleQuote.managers;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.ui.presenters.ReaderViewPresenter;
import com.google.android.gms.analytics.HitBuilders;

/**
 * @author Vladimir Yakushev
 * @version 1.0
 */
public final class GoogleAnalyticsHelper {
    private final static String CAATEGORY_BOOKMARKS = "bookmarks";
    private static final String CATEGORY_MODULES = "modules";
    private static final String CATEGORY_SEARCH = "search";
    private static final String CATEGORY_PARALLELS = "parallels";
    private static final String CATEGORY_HISTORY = "history";

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

    public void actionSendBookmark(Bookmark bookmark) {
        for(String tag : bookmark.tags.split(",")) {
            if (!"".equals(tag)) {
                createEvent(CAATEGORY_BOOKMARKS, "tags", tag);
            }
        }
        createEvent(CAATEGORY_BOOKMARKS, "bookmark", bookmark.OSISLink);
    }

    public void actionOpenLink(BibleReference reference, int openCode) {
        switch (openCode) {
            case ReaderViewPresenter.ID_BOOKMARKS:
                actionOpenBookmark(reference);
                break;
            case ReaderViewPresenter.ID_CHOOSE_CH:
                actionOpenChapter(reference);
                break;
            case ReaderViewPresenter.ID_HISTORY:
                actionOpenHistory(reference);
                break;
            case ReaderViewPresenter.ID_PARALLELS:
                actionOpenParallels(reference);
                break;
            case ReaderViewPresenter.ID_SEARCH:
                actionOpenSearchReference(reference);
                break;
        }
    }

    private void actionOpenSearchReference(BibleReference reference) {
        createEvent(CATEGORY_SEARCH, "open", reference.getPath());
    }

    private void actionOpenParallels(BibleReference reference) {
        createEvent(CATEGORY_PARALLELS, "open", reference.getPath());
    }

    private void actionOpenHistory(BibleReference reference) {
        createEvent(CATEGORY_HISTORY, "open", reference.getPath());
    }

    public void actionOpenBookmark(BibleReference osisLink) {
        createEvent(CAATEGORY_BOOKMARKS, "open", osisLink.getModuleID());
    }

    public void actionOpenChapter(BibleReference reference) {
        createEvent(CATEGORY_MODULES, "open_module", reference.getModuleID());
        createEvent(CATEGORY_MODULES, "open_book", reference.getBookID());
    }

    public void actionSearch(String moduleID, String query) {
        createEvent(CATEGORY_SEARCH, moduleID, query);
    }

    private void createEvent(String category, String action, String label) {
        BibleQuoteApp.getInstance().getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }
}
