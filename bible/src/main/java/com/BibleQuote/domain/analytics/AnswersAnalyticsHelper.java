package com.BibleQuote.domain.analytics;

import android.support.annotation.NonNull;

import com.BibleQuote.domain.AnalyticsHelper;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.ShareEvent;

/**
 * Отправка аналитики в Fabric
 *
 * @author Vladimir Yackushev <Yakushev.V.V@sberbank.ru>
 * @since 07/01/2019
 */
public class AnswersAnalyticsHelper implements AnalyticsHelper {

    private static final String CONTENT_TYPE_FEATURE = "feature";

    @Override
    public void moduleEvent(@NonNull BibleReference link) {
        Answers.getInstance().logCustom(
                new CustomEvent(CATEGORY_MODULES)
                        .putCustomAttribute(ATTR_MODULE, link.getModuleID())
                        .putCustomAttribute(ATTR_BOOK, link.getBookID())
        );
    }

    @Override
    public void bookmarkEvent(@NonNull Bookmark bookmark) {
        for (String tag : bookmark.tags.split(",")) {
            if (!"".equals(tag)) {
                Answers.getInstance().logCustom(
                        new CustomEvent(CATEGORY_ADD_TAGS)
                                .putCustomAttribute(ATTR_LINK, bookmark.OSISLink)
                                .putCustomAttribute(ATTR_OPEN_TAG, tag)
                );
            }
        }

        Answers.getInstance().logCustom(
                new CustomEvent(CATEGORY_ADD_BOOKMARK)
                        .putCustomAttribute(ATTR_LINK, bookmark.OSISLink)
        );
    }

    @Override
    public void clickEvent(@NonNull String action) {
        Answers.getInstance().logContentView(
                new ContentViewEvent()
                        .putContentType(CONTENT_TYPE_FEATURE)
                        .putContentName(action)
        );
    }

    @Override
    public void searchEvent(@NonNull String query, String module) {
        Answers.getInstance().logSearch(
                new SearchEvent()
                        .putQuery(query)
                        .putCustomAttribute(ATTR_MODULE, module)
        );
    }
}
