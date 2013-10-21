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

package com.BibleQuote.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.managers.bookmarks.BookmarksManager;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.ui.widget.listview.ItemAdapter;
import com.BibleQuote.ui.widget.listview.item.BookmarkItem;
import com.BibleQuote.ui.widget.listview.item.Item;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Vladimir Yakushev
 * Date: 14.05.13
 */
public class BookmarksFragment extends SherlockListFragment implements AdapterView.OnItemLongClickListener {
	private final static String TAG = BookmarksFragment.class.getSimpleName();

	private Librarian myLibrarian;
	private Bookmark currBookmark;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		setEmptyText(getResources().getText(R.string.empty));

		BibleQuoteApp app = (BibleQuoteApp) getSherlockActivity().getApplication();
		myLibrarian = app.getLibrarian();

		ListView lw = getListView();
		lw.setOnItemLongClickListener(this);

		setAdapter();
	}

	private BookmarksManager getBookmarksManager() {
		BibleQuoteApp app = (BibleQuoteApp) getSherlockActivity().getApplication();
		return new BookmarksManager(app.getBookmarksRepository());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.onBookmarsSelectListener = (OnBookmarkSelectListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnBookmarkSelectListener");
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i(TAG, "onCreateOptionsMenu");
		inflater.inflate(R.menu.menu_bookmarks, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_bar_delete:
				AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
				builder.setIcon(R.drawable.icon);
				builder.setTitle(R.string.bookmarks);
				builder.setMessage(R.string.fav_delete_all_question);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ItemAdapter adapter = (ItemAdapter) getListView().getAdapter();
						BookmarksManager bManager = getBookmarksManager();
						for (int i = 0; i < adapter.getCount(); i++) {
							Bookmark bookmark = ((BookmarkItem) adapter.getItem(i)).bookmark;
							bManager.delete(bookmark);
						}
						setAdapter();
					}
				});
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});
				builder.show();
				break;

			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView LV, View v, int position, long id) {
		currBookmark = ((BookmarkItem) LV.getAdapter().getItem(position)).bookmark;
		Log.i(TAG, "Select bookmark: " + currBookmark.humanLink + " (OSIS link = " + currBookmark.OSISLink + ")");

		BibleReference osisLink = new BibleReference(currBookmark.OSISLink);
		if (!myLibrarian.isOSISLinkValid(osisLink)) {
			Log.i(TAG, "Delete invalid bookmark: " + currBookmark);
			getBookmarksManager().delete(currBookmark);
			setAdapter();
			Toast.makeText(getSherlockActivity(), R.string.bookmark_invalid_removed, Toast.LENGTH_LONG).show();
		} else {
			alertListener(currBookmark);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		currBookmark = ((BookmarkItem) adapterView.getItemAtPosition(position)).bookmark;
		AlertDialog.Builder b = new AlertDialog.Builder(getSherlockActivity());
		b.setIcon(R.drawable.icon);
		b.setTitle(currBookmark.humanLink);
		b.setMessage(R.string.fav_question_del_fav);
		b.setPositiveButton("OK", positiveButton_OnClick);
		b.setNegativeButton(R.string.cancel, null);
		b.show();
		return true;
	}

	private DialogInterface.OnClickListener positiveButton_OnClick = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			Log.i(TAG, "Delete bookmark: " + currBookmark);
			getBookmarksManager().delete(currBookmark);
			setAdapter();
			Toast.makeText(getSherlockActivity(), R.string.removed, Toast.LENGTH_LONG).show();
		}
	};

	private void setAdapter() {
		List<Item> items = new ArrayList<Item>();
		for (Bookmark curr : getBookmarksManager().getAll()) {
			items.add(new BookmarkItem(curr));
		}
		ItemAdapter adapter = new ItemAdapter(getSherlockActivity(), items);
		setListAdapter(adapter);
	}

	private void setAdapter(Tag tag) {
		List<Item> items = new ArrayList<Item>();
		for (Bookmark curr : getBookmarksManager().getAll(tag)) {
			items.add(new BookmarkItem(curr));
		}
		ItemAdapter adapter = new ItemAdapter(getSherlockActivity(), items);
		setListAdapter(adapter);
	}

	public void setTagFilter(Tag tag) {
		setAdapter(tag);
	}

	public interface OnBookmarkSelectListener {
		void onBookmarksSelect(Bookmark OSISLink);
	}

	private OnBookmarkSelectListener onBookmarsSelectListener;
	public void setBookmarksListener(OnBookmarkSelectListener listener) {
		Log.i(TAG, "Set bookmarks onBookmarsSelectListener");
		this.onBookmarsSelectListener = listener;
	}

	private void alertListener(Bookmark OSISLink) {
		if (onBookmarsSelectListener != null) {
			onBookmarsSelectListener.onBookmarksSelect(OSISLink);
		}
	}
}
