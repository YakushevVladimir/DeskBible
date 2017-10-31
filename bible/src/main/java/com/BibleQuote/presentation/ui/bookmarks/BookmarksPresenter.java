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
 * File: BookmarksPresenter.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks;

import com.BibleQuote.R;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.managers.bookmarks.BookmarksManager;
import com.BibleQuote.presentation.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

public class BookmarksPresenter extends BasePresenter<BookmarksView> {

    private List<Bookmark> bookmarks;
    private BookmarksManager bookmarksManager;
    private Bookmark currBookmark;
    private Librarian myLibrarian;

    @Inject
    BookmarksPresenter(BookmarksManager bookmarksManager, Librarian myLibrarian) {
        this.bookmarksManager = bookmarksManager;
        this.myLibrarian = myLibrarian;
    }

    @Override
    public void onViewCreated() {
        updateBookmarks(null);
    }

    void onSetTag(Tag tag) {
        updateBookmarks(tag);
    }

    void onClickBookmarkDelete() {
        if (currBookmark == null) {
            return;
        }

        StaticLogger.info(this, "Delete bookmark: " + currBookmark);
        bookmarksManager.delete(currBookmark);
        getView().showToast(R.string.removed);
        updateBookmarks(null);
    }

    void onClickBookmarkEdit() {
        if (currBookmark != null) {
            getView().openBookmarkDialog(currBookmark);
        }
    }

    void onClickBookmarkOpen(int position) {
        if (position >= bookmarks.size()) {
            return;
        }

        Bookmark bookmark = bookmarks.get(position);
        BibleReference osisLink = new BibleReference(bookmark.OSISLink);
        if (!myLibrarian.isOSISLinkValid(osisLink)) { // модуль был удален и закладка больше не актуальна
            StaticLogger.info(this, "Delete invalid bookmark: " + position);
            getView().showToast(R.string.bookmark_invalid_removed);
            bookmarksManager.delete(bookmark);
            updateBookmarks(null);
        } else {
            getView().openBookmark(bookmark);
        }
    }

    void onSelectBookmark(int position) {
        if (position < bookmarks.size()) {
            currBookmark = bookmarks.get(position);
            getView().startBookmarkAction(currBookmark.name);
        }
    }

    void onRefresh() {
        updateBookmarks(null);
    }

    void removeBookmarks() {
        for (Bookmark bookmark : bookmarks) {
            bookmarksManager.delete(bookmark);
        }
        updateBookmarks(null);
    }

    private void updateBookmarks(Tag tag) {
        bookmarks = bookmarksManager.getAll(tag);
        currBookmark = null;
        getView().updateBookmarks(bookmarks);
    }
}
