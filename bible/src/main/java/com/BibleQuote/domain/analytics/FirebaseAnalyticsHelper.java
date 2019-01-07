package com.BibleQuote.domain.analytics;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.BibleQuote.domain.AnalyticsHelper;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Отправка аналитики в Google Firebase
 *
 * @author Vladimir Yackushev <Yakushev.V.V@sberbank.ru>
 * @since 07/01/2019
 */
public class FirebaseAnalyticsHelper implements AnalyticsHelper {

    private final FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseAnalyticsHelper(@NonNull FirebaseAnalytics firebaseAnalytics) {
        mFirebaseAnalytics = firebaseAnalytics;
    }

    @Override
    public void moduleEvent(@NonNull BibleReference link) {
        Bundle bundle = new Bundle();
        bundle.putString(ATTR_MODULE, link.getModuleID());
        bundle.putString(ATTR_BOOK, link.getBookID());
        mFirebaseAnalytics.logEvent(CATEGORY_MODULES, bundle);
    }

    @Override
    public void bookmarkEvent(@NonNull Bookmark bookmark) {
        for (String tag : bookmark.tags.split(",")) {
            if (!"".equals(tag)) {
                Bundle bundle = new Bundle();
                bundle.putString(ATTR_LINK, bookmark.OSISLink);
                bundle.putString(ATTR_OPEN_TAG, tag);
                mFirebaseAnalytics.logEvent(CATEGORY_ADD_TAGS, bundle);
            }
        }

        Bundle bundle = new Bundle();
        bundle.putString(ATTR_LINK, bookmark.OSISLink);
        mFirebaseAnalytics.logEvent(CATEGORY_ADD_BOOKMARK, bundle);
    }

    @Override
    public void clickEvent(@NonNull String action) {
        Bundle bundle = new Bundle();
        bundle.putString(ATTR_ACTION, action);
        mFirebaseAnalytics.logEvent(CATEGORY_CLICK, bundle);
    }

    @Override
    public void searchEvent(@NonNull String query, String module) {
        Bundle bundle = new Bundle();
        bundle.putString(ATTR_QUERY, query);
        bundle.putString(ATTR_MODULE, module);
        mFirebaseAnalytics.logEvent(CATEGORY_SEARCH, bundle);
    }
}
