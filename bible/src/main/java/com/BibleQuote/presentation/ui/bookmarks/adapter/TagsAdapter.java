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
 * File: TagsAdapter.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks.adapter;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BibleQuote.R;
import com.BibleQuote.domain.entity.TagWithCount;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TagsAdapter extends ClickableListAdapter<TagWithCount> {

    public TagsAdapter(@NonNull List<TagWithCount> items, @NonNull OnClickListener clickListener) {
        super(items, clickListener);
    }

    @Override
    public ListViewHolder<TagWithCount> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getView(parent, R.layout.item_tag);
        return new TagViewHolder(view);
    }

    public static class TagViewHolder extends ListViewHolder<TagWithCount> {

        @BindView(R.id.tag_name) TextView viewName;
        @BindView(R.id.tag_count) TextView viewCount;

        TagViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(TagWithCount tag) {
            if (tag != null) {
                viewName.setText(tag.tag().toString());
                viewCount.setText(tag.count());
            }
        }
    }
}
