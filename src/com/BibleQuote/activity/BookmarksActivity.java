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
package com.BibleQuote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.BibleQuote.R;
import com.BibleQuote.fragments.BookmarksFragment;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.utils.ViewUtils;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;

public class BookmarksActivity extends SherlockFragmentActivity implements ActionBar.TabListener, BookmarksFragment.IBookmarksListener {

	private final String TAG = BookmarksActivity.class.getSimpleName();

	private BookmarksFragment bmFragment = new BookmarksFragment();
	private ActionBar.Tab bmTab, tagTab;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks_activity);

		ViewUtils.setActionBarBackground(this);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		bmFragment.setBookmarksListener(this);

		bmTab = getSupportActionBar().newTab();
		bmTab.setText(R.string.bookmarks);
		bmTab.setTabListener(this);
		getSupportActionBar().addTab(bmTab, true);

		tagTab = getSupportActionBar().newTab();
		tagTab.setText(R.string.tags);
		tagTab.setTabListener(this);
		getSupportActionBar().addTab(tagTab);
	}

	private void setFragment(ActionBar.Tab currTab) {
		SherlockListFragment currFragment;
		if (currTab.getText().equals(getResources().getText(R.string.bookmarks))) {
			currFragment = bmFragment;
		} else if (currTab.getText().equals(getResources().getText(R.string.tags))) {
			currFragment = bmFragment;
		} else {
			Log.e(TAG, "ERROR undefined tab: " + currTab.getText());
			return;
		}

		FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
		tran.replace(R.id.frame, currFragment);
		tran.addToBackStack(null);
		tran.commit();
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		setFragment(tab);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}

	@Override
	public void onBookmarksSelect(Bookmark bookmark) {
		Intent intent = new Intent();
		intent.putExtra("linkOSIS", bookmark.OSISLink);
		setResult(RESULT_OK, intent);
		finish();
	}
}
