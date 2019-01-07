package com.BibleQuote.domain.analytics;

import android.support.annotation.NonNull;

import com.BibleQuote.domain.AnalyticsHelper;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;

import java.util.Collections;
import java.util.List;

/**
 * Отправка аналитики сразу в несколько источников
 *
 * @author Vladimir Yackushev <Yakushev.V.V@sberbank.ru>
 * @since 07/01/2019
 */
public class CompositeAnalyticsHelper implements AnalyticsHelper {

    private final List<AnalyticsHelper> mHelpers;

    public CompositeAnalyticsHelper(List<AnalyticsHelper> helpers) {
        mHelpers = Collections.unmodifiableList(helpers);
    }

    @Override
    public void moduleEvent(@NonNull BibleReference link) {
        for (AnalyticsHelper helper : mHelpers) {
            helper.moduleEvent(link);
        }
    }

    @Override
    public void bookmarkEvent(@NonNull Bookmark bookmark) {
        for (AnalyticsHelper helper : mHelpers) {
            helper.bookmarkEvent(bookmark);
        }
    }

    @Override
    public void clickEvent(@NonNull String action) {
        for (AnalyticsHelper helper : mHelpers) {
            helper.clickEvent(action);
        }
    }

    @Override
    public void searchEvent(@NonNull String query, String module) {
        for (AnalyticsHelper helper : mHelpers) {
            helper.searchEvent(query, module);
        }
    }
}
