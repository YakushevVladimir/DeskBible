/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.managers;

import android.app.Activity;
import android.content.Context;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.ui.ReaderActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * @author Vladimir Yakushev
 * @version 1.0
 */
public class GoogleAnalyticsHelper {
    private final static String CAATEGORY_BOOKMARKS = "bookmarks";
    private static final String CATEGORY_MODULES = "modules";
    private static final String CATEGORY_SEARCH = "search";
    private static final String CATEGORY_PARALLELS = "parallels";
    private static final String CATEGORY_HISTORY = "history";

    private Context context;
    private static volatile GoogleAnalyticsHelper instance;

    private GoogleAnalyticsHelper(Context context) {
        this.context = context;
    }

    public static GoogleAnalyticsHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (GoogleAnalyticsHelper.class) {
                if (instance == null) {
                    instance = new GoogleAnalyticsHelper(context);
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
            case ReaderActivity.ID_BOOKMARKS:
                actionOpenBookmark(reference);
                break;
            case ReaderActivity.ID_CHOOSE_CH:
                actionOpenChapter(reference);
                break;
            case ReaderActivity.ID_HISTORY:
                actionOpenHistory(reference);
                break;
            case ReaderActivity.ID_PARALLELS:
                actionOpenParallels(reference);
                break;
            case ReaderActivity.ID_SEARCH:
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
        EasyTracker.getInstance(context).send(
                MapBuilder.createEvent(category, action, label, null).build()
        );
    }

    public void startActivity(Activity activity) {
        EasyTracker.getInstance(context).activityStart(activity);
    }

    public void stopActivity(Activity activity) {
        EasyTracker.getInstance(context).activityStop(activity);
    }
}
