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
 * File: BookmarksFragment.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BibleQuote.R;
import com.BibleQuote.di.component.FragmentComponent;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.presentation.dialogs.BookmarksDialog;
import com.BibleQuote.presentation.ui.base.BaseFragment;
import com.BibleQuote.presentation.ui.bookmarks.adapter.BookmarkAdapter;
import com.BibleQuote.presentation.ui.bookmarks.adapter.ClickableListAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.churchtools.deskbible.domain.logger.StaticLogger;

/**
 * User: Vladimir Yakushev
 * Date: 14.05.13
 */
public class BookmarksFragment extends BaseFragment<BookmarksPresenter> implements BookmarksView {

    @BindView(R.id.list_bookmarks)
    RecyclerView viewBookmarksList;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        unbinder = ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        viewBookmarksList.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewBookmarksList.setHasFixedSize(true);
        viewBookmarksList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));

        try {
            presenter.setChangeListener((OnBookmarksChangeListener) getActivity());
        } catch (ClassCastException e) {
            throw new ClassCastException(requireActivity().toString() + " must implement OnBookmarksChangeListener");
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        StaticLogger.info(this, "onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_bookmarks, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(R.string.bookmarks);
                builder.setMessage(R.string.fav_delete_all_question);
                builder.setPositiveButton("OK", (dialog, which) -> presenter.removeBookmarks());
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
                break;

            case R.id.action_bar_refresh:
                presenter.onRefresh();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }

    @Override
    protected void attachView() {
        presenter.attachView(this);
    }

    @Override
    public void setTagFilter(Tag tag) {
        presenter.onSetTag(tag);
    }

    @Override
    public void startBookmarkAction(String title) {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionMode currActionMode = activity
                .startSupportActionMode(new BookmarksSelectAction(activity, presenter));
        if (currActionMode != null) {
            currActionMode.setTitle(title);
        }
    }

    @Override
    public void openBookmarkDialog(Bookmark bookmark) {
        DialogFragment bmDial = BookmarksDialog.newInstance(bookmark);
        bmDial.show(requireActivity().getSupportFragmentManager(), "bookmark");
    }

    @Override
    public void updateBookmarks(@NonNull final List<Bookmark> bookmarks) {
        viewBookmarksList.setAdapter(new BookmarkAdapter(bookmarks, new ClickableListAdapter.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewBookmarksList.getChildAdapterPosition(v);
                presenter.onClickBookmarkOpen(position);
            }

            @Override
            public void onLongClick(View v) {
                int position = viewBookmarksList.getChildAdapterPosition(v);
                presenter.onSelectBookmark(position);
            }
        }));
    }

    @Override
    public void refreshBookmarks() {
        presenter.onRefresh();
    }

    private static class BookmarksSelectAction implements ActionMode.Callback {

        private final Context context;
        private final BookmarksPresenter presenter;
        private final MenuInflater menuInflater;

        BookmarksSelectAction(Activity activity, BookmarksPresenter presenter) {
            this.presenter = presenter;
            this.context = activity;
            this.menuInflater = activity.getMenuInflater();
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menuInflater.inflate(R.menu.menu_action_bookmark_select, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    presenter.onClickBookmarkEdit();
                    break;
                case R.id.action_delete:
                    new AlertDialog.Builder(context)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setMessage(R.string.fav_question_del_fav)
                            .setPositiveButton("OK", (dialog, which) -> presenter.onClickBookmarkDelete())
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                    break;
                default:
                    return false;
            }

            mode.finish();
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }
    }
}
