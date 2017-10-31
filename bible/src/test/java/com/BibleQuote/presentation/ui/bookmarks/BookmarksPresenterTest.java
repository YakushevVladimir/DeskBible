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
 * File: BookmarksPresenterTest.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks;

import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.managers.bookmarks.BookmarksManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
public class BookmarksPresenterTest {

    @Mock BookmarksManager bookmarksManager;
    @Mock Librarian librarian;
    @Mock BookmarksView view;
    @Mock OnBookmarksChangeListener changeListener;

    private List<Bookmark> bookmarksWithoutTag;
    private List<Bookmark> bookmarksWithTag;

    private BookmarksPresenter presenter;
    private ArgumentCaptor<BookmarksList> bmCaptor = ArgumentCaptor.forClass(BookmarksList.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        presenter = new BookmarksPresenter(bookmarksManager, librarian);
        presenter.attachView(view);

        bookmarksWithoutTag = new ArrayList<>();
        bookmarksWithoutTag.add(new Bookmark(1L, "Genesis 1", "RST.Gen.1.1", "Gen 1:1", "23.10.2017"));
        bookmarksWithoutTag.add(new Bookmark(2L, "Genesis 2", "RST.Gen.2.1", "Gen 2:1", "24.10.2017"));
        bookmarksWithoutTag.add(new Bookmark(3L, "Genesis 3", "RST.Gen.3.1", "Gen 3:1", "26.10.2017"));
        bookmarksWithoutTag.add(new Bookmark(4L, "Genesis 4", "RST.Gen.4.1", "Gen 4:1", "25.10.2017"));

        bookmarksWithTag = Collections.singletonList(
                new Bookmark(2L, "Genesis 2", "RST.Gen.2.1", "Gen 2:1", "24.10.2017")
        );

        when(bookmarksManager.getAll(isNull())).thenReturn(bookmarksWithoutTag);
        when(bookmarksManager.getAll(any(Tag.class))).thenReturn(bookmarksWithTag);
    }

    @Test(expected = IllegalStateException.class)
    public void onViewCreatedWithoutChangeListener() throws Exception {
        presenter.onViewCreated();
    }

    @Test
    public void onViewCreated() throws Exception {
        presenter.setChangeListener(changeListener);
        presenter.onViewCreated();

        verify(bookmarksManager).getAll(isNull());
        verify(view).updateBookmarks(bmCaptor.capture());

        List<Bookmark> bookmarksRes = bmCaptor.getValue();
        assertNotNull(bookmarksRes);
        assertThat(bookmarksRes.size(), equalTo(bookmarksWithoutTag.size()));
    }

    @Test
    public void onSetTag() throws Exception {
        Tag tag = new Tag(1, "abraham");

        presenter.onSetTag(tag);

        verify(bookmarksManager).getAll(eq(tag));
        verify(view).updateBookmarks(bmCaptor.capture());

        List<Bookmark> bookmarks = bmCaptor.getValue();
        assertNotNull(bookmarks);
        assertThat(bookmarks.size(), equalTo(bookmarksWithTag.size()));
    }

    @Test
    public void onClickBookmarkDeleteWithoutSelection() throws Exception {
        presenter.onRefresh(); // заполняем презентер списком закладок
        Mockito.reset(view); // сбрасываем обращения к view

        presenter.onClickBookmarkDelete();

        verify(view, never()).updateBookmarks(anyList());
        verify(bookmarksManager, never()).delete(any(Bookmark.class));
    }

    @Test
    public void onClickBookmarkDelete() throws Exception {
        presenter.setChangeListener(changeListener);
        presenter.onRefresh(); // заполняем презентер списком закладок
        Mockito.reset(view); // сбрасываем обращения к view

        presenter.onSelectBookmark(0);
        presenter.onClickBookmarkDelete();

        verify(bookmarksManager).delete(eq(bookmarksWithoutTag.get(0)));
        verify(changeListener).onBookmarksUpdate();
        verify(view).updateBookmarks(anyList());
    }

    @Test
    public void onClickBookmarkEditWithoutSelection() throws Exception {
        presenter.onRefresh(); // заполняем презентер списком закладок
        Mockito.reset(view); // сбрасываем обращения к view

        presenter.onClickBookmarkEdit();

        verify(view, never()).openBookmarkDialog(any(Bookmark.class));
    }

    @Test
    public void onClickBookmarkEdit() throws Exception {
        presenter.onRefresh(); // заполняем презентер списком закладок
        Mockito.reset(view); // сбрасываем обращения к view

        presenter.onSelectBookmark(0);
        presenter.onClickBookmarkEdit();

        verify(view).openBookmarkDialog(eq(bookmarksWithoutTag.get(0)));
    }

    @Test
    public void onClickBookmarkOpenWithIllegalPosition() throws Exception {
        presenter.onRefresh(); // заполняем презентер списком закладок
        Mockito.reset(view); // сбрасываем обращения к view

        presenter.onClickBookmarkOpen(bookmarksWithoutTag.size());

        verify(changeListener, never()).onBookmarksSelect(any(Bookmark.class));
        verify(view, never()).updateBookmarks(anyList());
    }

    @Test
    public void onClickBookmarkOpen() throws Exception {
        when(librarian.isOSISLinkValid(any())).thenReturn(true);

        presenter.setChangeListener(changeListener);
        presenter.onRefresh();
        presenter.onClickBookmarkOpen(0);

        verify(changeListener).onBookmarksSelect(eq(bookmarksWithoutTag.get(0)));
    }

    @Test
    public void onClickBookmarkOpenWithIncorrectBookmark() throws Exception {
        when(librarian.isOSISLinkValid(any())).thenReturn(false);

        presenter.setChangeListener(changeListener);
        presenter.onRefresh(); // заполняем презентер списком закладок
        Mockito.reset(view); // сбрасываем обращения к view

        presenter.onClickBookmarkOpen(0);

        verify(changeListener).onBookmarksUpdate();
        verify(changeListener, never()).onBookmarksSelect(any(Bookmark.class));
        verify(view).updateBookmarks(anyList());
        verify(view).showToast(anyInt());
        verify(bookmarksManager).delete(any(Bookmark.class));
    }

    @Test
    public void onSelectBookmarkWithIllegalPosition() throws Exception {
        presenter.onRefresh();
        presenter.onSelectBookmark(bookmarksWithoutTag.size());
        verify(view, never()).startBookmarkAction(anyString());
    }

    @Test
    public void onSelectBookmark() throws Exception {
        presenter.onRefresh();
        presenter.onSelectBookmark(0);
        verify(view).startBookmarkAction(eq(bookmarksWithoutTag.get(0).name));
    }

    @Test
    public void onRefresh() throws Exception {
        presenter.onRefresh();

        verify(view).updateBookmarks(bmCaptor.capture());

        List<Bookmark> bookmarks = bmCaptor.getValue();
        assertNotNull(bookmarks);
        assertThat(bookmarks.size(), equalTo(bookmarksWithoutTag.size()));
    }

    @Test
    public void removeBookmarks() throws Exception {
        doAnswer(invocation -> {
            when(bookmarksManager.getAll(isNull())).thenReturn(Collections.emptyList());
            return null;
        }).when(bookmarksManager).delete(any(Bookmark.class));

        presenter.setChangeListener(changeListener);
        presenter.onRefresh(); // заполняем презентер списком закладок
        Mockito.reset(view); // сбрасываем обращения к view

        presenter.removeBookmarks();

        verify(view).updateBookmarks(bmCaptor.capture());
        verify(changeListener).onBookmarksUpdate();

        List<Bookmark> bookmarks = bmCaptor.getValue();
        assertNotNull(bookmarks);
        assertThat(bookmarks.size(), equalTo(0));
    }

    private static class BookmarksList extends ArrayList<Bookmark> {

    }
}