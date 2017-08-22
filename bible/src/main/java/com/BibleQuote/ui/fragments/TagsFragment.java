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
 * File: TagsFragment.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.BibleQuote.R;
import com.BibleQuote.dal.repository.bookmarks.DbTagRepository;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.managers.tags.TagsManager;
import com.BibleQuote.ui.widget.listview.ItemAdapter;
import com.BibleQuote.ui.widget.listview.item.Item;
import com.BibleQuote.ui.widget.listview.item.TagItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Vladimir Yakushev
 * Date: 14.05.13
 */
public class TagsFragment extends ListFragment implements AdapterView.OnItemLongClickListener {

    private final static String TAG = TagsFragment.class.getSimpleName();
    private final TagsManager tagManager = new TagsManager(new DbTagRepository());
    private OnTagsChangeListener onTagsChangeListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setEmptyText(getResources().getText(R.string.empty));

        ListView lw = getListView();
        lw.setLongClickable(true);
        lw.setOnItemLongClickListener(this);

        setAdapter();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            setOnTagSelectListener((OnTagsChangeListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_tags, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_refresh:
                setAdapter();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        final Tag currTag = ((TagItem) lv.getAdapter().getItem(position)).tag;
        onTagSelectListenerAlert(currTag);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        final Tag currTag = ((TagItem) adapterView.getItemAtPosition(position)).tag;
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setIcon(R.mipmap.ic_launcher);
        b.setTitle(currTag.name);
        b.setMessage(R.string.question_del_tag);
        b.setPositiveButton("OK", (dialog, which) -> {
            tagManager.delete(currTag);
            setAdapter();
            onTagsUppdateListenerAlert();
        });
        b.setNegativeButton(R.string.cancel, null);
        b.show();
        return true;
    }

    private void setAdapter() {
        List<Item> items = new ArrayList<>();
        LinkedHashMap<Tag, String> tagList = tagManager.getAllWithCount();
        for (Map.Entry<Tag, String> entry : tagList.entrySet()) {
            items.add(new TagItem(entry.getKey(), entry.getValue()));
        }
        ItemAdapter adapter = new ItemAdapter(getActivity(), items);
        setListAdapter(adapter);
    }

    public void updateTags() {
        setAdapter();
    }

    public void setOnTagSelectListener(OnTagsChangeListener listener) {
        this.onTagsChangeListener = listener;
    }

    private void onTagSelectListenerAlert(Tag tag) {
        if (this.onTagsChangeListener != null) {
            this.onTagsChangeListener.onTagSelect(tag);
        }
    }

    private void onTagsUppdateListenerAlert() {
        if (this.onTagsChangeListener != null) {
            this.onTagsChangeListener.onTagsUpdate();
        }
    }

    public interface OnTagsChangeListener {
        void onTagSelect(Tag tag);

        void onTagsUpdate();
    }
}
