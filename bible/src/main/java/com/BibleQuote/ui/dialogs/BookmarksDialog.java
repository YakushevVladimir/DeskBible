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
 * File: BookmarksDialog.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.dal.repository.bookmarks.DbBookmarksTagsRepository;
import com.BibleQuote.dal.repository.bookmarks.DbTagRepository;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.managers.GoogleAnalyticsHelper;
import com.BibleQuote.managers.bookmarks.BookmarksManager;
import com.BibleQuote.ui.BookmarksActivity;

public class BookmarksDialog extends DialogFragment {

    private Bookmark bookmark;
    private TextView tvDate, tvHumanLink;
    private EditText tvName, tvTags;
    private IBookmarksRepository bookmarksRepository;

    public static BookmarksDialog newInstance(Bookmark bookmark) {
        // TODO: 29.09.17 переделать на Bundle
        BookmarksDialog result = new BookmarksDialog();
        result.setBookmark(bookmark);
        return result;
    }

    public BookmarksDialog() {
        super();
        bookmarksRepository = BibleQuoteApp.getInstance().getBookmarksRepository();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.bookmarks)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> addBookmarks())
                .setNegativeButton(android.R.string.cancel, null);
        View customView = inflater.inflate(R.layout.bookmarks_dialog, null);
        builder.setView(customView);

        tvDate = (TextView) customView.findViewById(R.id.bm_date);
        tvHumanLink = (TextView) customView.findViewById(R.id.bm_humanLink);
        tvName = (EditText) customView.findViewById(R.id.bm_name);
        tvTags = (EditText) customView.findViewById(R.id.bm_tags);

        fillField();

        return builder.create();
    }

    private void addBookmarks() {
        readField();
        new BookmarksManager(bookmarksRepository, new DbBookmarksTagsRepository(), new DbTagRepository()).add(bookmark, bookmark.tags);

        GoogleAnalyticsHelper.getInstance().actionSendBookmark(bookmark);

        if (getActivity() instanceof BookmarksActivity) {
            ((BookmarksActivity) getActivity()).onBookmarksUpdate();
            ((BookmarksActivity) getActivity()).onTagsUpdate();
        }

        Toast.makeText(getActivity(), getString(R.string.added), Toast.LENGTH_LONG).show();
        dismiss();
    }

    private void fillField() {
        if (bookmark != null) {
            tvDate.setText(bookmark.date);
            tvHumanLink.setText(bookmark.humanLink);
            tvName.setText(bookmark.name);
            tvTags.setText(bookmark.tags);
        }
    }

    private void readField() {
        if (bookmark != null) {
            bookmark.humanLink = tvHumanLink.getText().toString();
            bookmark.name = tvName.getText().toString();
            bookmark.date = tvDate.getText().toString();
            bookmark.tags = tvTags.getText().toString();
        }
    }

    public void setBookmark(Bookmark bookmark) {
        this.bookmark = bookmark;
    }
}
