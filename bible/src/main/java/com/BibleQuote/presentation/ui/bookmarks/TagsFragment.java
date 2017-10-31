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
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.BibleQuote.R;
import com.BibleQuote.di.component.FragmentComponent;
import com.BibleQuote.domain.entity.TagWithCount;
import com.BibleQuote.presentation.ui.base.BaseFragment;
import com.BibleQuote.presentation.ui.bookmarks.adapter.ClickableListAdapter;
import com.BibleQuote.presentation.ui.bookmarks.adapter.TagsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TagsFragment extends BaseFragment<TagsPresenter> implements TagsView {

    @BindView(R.id.tags_list) RecyclerView viewTagsList;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);

        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        viewTagsList.setLayoutManager(new LinearLayoutManager(getContext()));
        viewTagsList.setHasFixedSize(true);
        viewTagsList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        try {
            presenter.setChangeListener((OnTagsChangeListener) getActivity());
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnTagsChangeListener");
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
                presenter.refreshTags();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateTags(List<TagWithCount> items) {
        if (viewTagsList == null) {
            return;
        }

        viewTagsList.setAdapter(new TagsAdapter(items, new ClickableListAdapter.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewTagsList.getChildAdapterPosition(v);
                presenter.onTagSelected(pos);
            }

            @Override
            public void onLongClick(View v) {
                int pos = viewTagsList.getChildAdapterPosition(v);
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.question_del_tag)
                        .setPositiveButton("OK", (dialog, which) -> presenter.onDeleteTag(pos))
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        }));
    }

    @Override
    public void refreshTags() {
        presenter.refreshTags();
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }

    @Override
    protected void attachView() {
        presenter.attachView(this);
    }
}
