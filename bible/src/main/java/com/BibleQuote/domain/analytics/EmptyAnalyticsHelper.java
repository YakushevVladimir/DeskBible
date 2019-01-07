package com.BibleQuote.domain.analytics;

import com.BibleQuote.domain.AnalyticsHelper;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;

public class EmptyAnalyticsHelper implements AnalyticsHelper {

    @Override
    public void moduleEvent(BibleReference link) {

    }

    @Override
    public void bookmarkEvent(Bookmark bookmark) {

    }

    @Override
    public void clickEvent(String action, String label) {

    }

    @Override
    public void searchEvent(String query, String module) {

    }
}
