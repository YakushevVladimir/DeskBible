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
 * File: ClickableListAdapter.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class ClickableListAdapter<T> extends RecyclerView.Adapter<ClickableListAdapter.ListViewHolder<T>> {

    private OnClickListener clickListener;
    private List<T> items;

    ClickableListAdapter(@NonNull List<T> items, @NonNull OnClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(ListViewHolder<T> holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    View getView(ViewGroup parent, int resId) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
        view.setOnClickListener(v -> clickListener.onClick(v));
        view.setOnLongClickListener(v -> {
            clickListener.onLongClick(v);
            return true;
        });
        return view;
    }

    public interface OnClickListener {

        void onClick(View v);

        void onLongClick(View v);
    }

    abstract static class ListViewHolder<T> extends RecyclerView.ViewHolder {

        ListViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(T bookmark);
    }
}
