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
 * File: BookmarksAdapter.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BibleQuote.R;
import com.BibleQuote.domain.entity.Bookmark;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkViewHolder> {

    private List<Bookmark> bookmarks;
    private View.OnClickListener clickListener;
    private View.OnLongClickListener longClickListener;

    BookmarksAdapter(List<Bookmark> bookmarks, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        this.bookmarks = bookmarks;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false);
        view.setOnClickListener(clickListener);
        view.setOnLongClickListener(longClickListener);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookmarkViewHolder holder, int position) {
        holder.bind(bookmarks.get(position));
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bookmark_link) TextView viewLink;
        @BindView(R.id.bookmark_date) TextView viewDate;
        @BindView(R.id.bookmark_title) TextView viewTitle;
        @BindView(R.id.bookmark_tags) TextView viewTags;

        BookmarkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Bookmark bookmark) {
            if (bookmark != null) {
                viewLink.setText(bookmark.humanLink);
                viewDate.setText(bookmark.date);
                viewTitle.setText(bookmark.name);
                if (!TextUtils.isEmpty(bookmark.tags)) {
                    viewTags.setText(bookmark.tags);
                }
            }
        }
    }
}
