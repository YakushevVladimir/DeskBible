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
 * File: BookmarksActivity.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.BibleQuote.R;
import com.BibleQuote.di.component.ActivityComponent;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.presentation.ui.base.BQActivity;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends BQActivity
        implements BookmarksFragment.OnBookmarksChangeListener, TagsFragment.OnTagsChangeListener {

    public static final String EXTRA_MODE = "extra_mode";
    public static final String MODE_TAGS = "tags";
    public static final String MODE_BOOKMARKS = "bookmarks";

    private static final String KEY_TAB = "tab";
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addPage(getString(R.string.bookmarks), new BookmarksFragment());
        pagerAdapter.addPage(getString(R.string.tags), new TagsFragment());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MODE)) {
            final String mode = intent.getStringExtra(EXTRA_MODE);
            viewPager.setCurrentItem(mode.equals(MODE_BOOKMARKS) ? 0 : 1);
        } else if (savedInstanceState != null) {
            viewPager.setCurrentItem(savedInstanceState.getInt(KEY_TAB));
        }
    }

    @Override
    protected void inject(ActivityComponent component) {
        component.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TAB, viewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        viewPager.setCurrentItem(state.getInt(KEY_TAB));
    }

    @Override
    public void onBookmarksSelect(Bookmark bookmark) {
        Intent intent = new Intent();
        intent.putExtra("linkOSIS", bookmark.OSISLink);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBookmarksUpdate() {
        ((TagsFragment) pagerAdapter.getItem(1)).updateTags();
    }

    @Override
    public void onTagSelect(Tag tag) {
        viewPager.setCurrentItem(0);
        ((BookmarksFragment) pagerAdapter.getItem(0)).setTagFilter(tag);
    }

    @Override
    public void onTagsUpdate() {
        ((BookmarksFragment) pagerAdapter.getItem(0)).updateBookmarks();
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> pages = new ArrayList<>();
        private List<String> titles = new ArrayList<>();

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public Fragment getItem(int position) {
            if (pages.size() > position) {
                return pages.get(position);
            }
            return new Fragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (titles.size() > position) {
                return titles.get(position);
            }
            return "";
        }

        void addPage(String name, Fragment page) {
            titles.add(name);
            pages.add(page);
        }

    }
}
