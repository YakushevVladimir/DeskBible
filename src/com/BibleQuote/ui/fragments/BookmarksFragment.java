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
import com.BibleQuote.ui.BookmarksActivity;
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

	private BookmarksManager bookmarksManager;
	private Librarian myLibrarian;
	private Bookmark currBookmark;

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.bookmarks_fragment, null);
//	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		setEmptyText(getResources().getText(R.string.empty));

		BibleQuoteApp app = (BibleQuoteApp) getSherlockActivity().getApplication();
		myLibrarian = app.getLibrarian();
		bookmarksManager = new BookmarksManager(app.getBookmarksRepository());

		ListView lw = getListView();
		lw.setOnItemLongClickListener(this);

		setAdapter();
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
			case R.id.action_bar_sort:
				bookmarksManager.sort();
				setAdapter();
				break;

			case R.id.action_bar_delete:
				AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
				builder.setIcon(R.drawable.icon);
				builder.setTitle(R.string.bookmarks);
				builder.setMessage(R.string.fav_delete_all_question);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						bookmarksManager.deleteAll();
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
			bookmarksManager.delete(currBookmark);
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
			bookmarksManager.delete(currBookmark);
			setAdapter();
			Toast.makeText(getSherlockActivity(), R.string.removed, Toast.LENGTH_LONG).show();
		}
	};

	private void setAdapter() {
		List<Item> items = new ArrayList<Item>();
		for (Bookmark curr : bookmarksManager.getAll()) {
			items.add(new BookmarkItem(curr));
		}
		ItemAdapter adapter = new ItemAdapter(getSherlockActivity(), items);
		setListAdapter(adapter);
	}


	public interface IBookmarksListener {
		void onBookmarksSelect(Bookmark OSISLink);
	}

	private IBookmarksListener listener;

	public void setBookmarksListener(IBookmarksListener listener) {
		Log.i(TAG, "Set bookmarks listener");
		this.listener = listener;
	}

	private void alertListener(Bookmark OSISLink) {
		//if (listener == null) return;
		Log.i(TAG, "Alert listener");
		((IBookmarksListener) getSherlockActivity()).onBookmarksSelect(OSISLink);
	}
}
