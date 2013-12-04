/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import com.BibleQuote.R;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.ui.adapters.TabsAdapter;
import com.BibleQuote.ui.fragments.BookmarksFragment;
import com.BibleQuote.ui.fragments.TagsFragment;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BookmarksActivity extends SherlockFragmentActivity
		implements BookmarksFragment.OnBookmarksChangeListener, TagsFragment.OnTagsChangeListener {

	private TabHost mTabHost;
	private TabsAdapter mTabsAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks_activity);

		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();

		ViewPager mViewPager = (ViewPager)findViewById(R.id.pager);

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
		mTabsAdapter.addTab(mTabHost.newTabSpec("bookmarks").setIndicator(getTabIndicator(R.string.bookmarks)),
				new BookmarksFragment(), null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("tags").setIndicator(getTabIndicator(R.string.tags)),
				new TagsFragment(), null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	private View getTabIndicator(int stringID) {
		View tabView = LayoutInflater.from(this).inflate(R.layout.tab_indicator, null);
		TextView tv = (TextView) tabView.findViewById(R.id.tab);
		tv.setText(getResources().getText(stringID).toString().toUpperCase());
		return tabView;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
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
		((TagsFragment) mTabsAdapter.getItem(1)).updateTags();
	}

	@Override
	public void onTagSelect(Tag tag) {
		mTabHost.setCurrentTab(0);
		((BookmarksFragment) mTabsAdapter.getItem(0)).setTagFilter(tag);
	}

	@Override
	public void onTagsUpdate() {
		((BookmarksFragment) mTabsAdapter.getItem(0)).updateBookmarks();
	}
}
