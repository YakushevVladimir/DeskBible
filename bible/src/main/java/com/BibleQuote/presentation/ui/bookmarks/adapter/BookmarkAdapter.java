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
 * File: BookmarkAdapter.java
 *
 * Created by Vladimir Yakushev at 11/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BibleQuote.R;
import com.BibleQuote.domain.entity.Bookmark;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarkAdapter extends ClickableListAdapter<Bookmark> {

    public BookmarkAdapter(@NonNull List<Bookmark> items, @NonNull ClickableListAdapter.OnClickListener clickListener) {
        super(items, clickListener);
    }

    @Override
    public ListViewHolder<Bookmark> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getView(parent, R.layout.item_bookmark);
        return new BookmarkViewHolder(view);
    }

    public static class BookmarkViewHolder extends ListViewHolder<Bookmark> {

        @BindView(R.id.bookmark_link) TextView viewLink;
        @BindView(R.id.bookmark_time) TextView viewTime;
        @BindView(R.id.bookmark_title) TextView viewTitle;
        @BindView(R.id.bookmark_tags) TextView viewTags;

        BookmarkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(Bookmark bookmark) {
            if (bookmark != null) {
                viewLink.setText(bookmark.humanLink);
                viewTime.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(bookmark.time));
                viewTitle.setText(bookmark.name);
                if (!TextUtils.isEmpty(bookmark.tags)) {
                    viewTags.setText(bookmark.tags);
                }
            }
        }
    }
}
