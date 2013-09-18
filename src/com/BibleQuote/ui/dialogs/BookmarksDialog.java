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

package com.BibleQuote.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.BibleQuote.R;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.managers.bookmarks.BookmarksManager;
import com.BibleQuote.managers.bookmarks.repository.dbBookmarksRepository;
import com.actionbarsherlock.app.SherlockDialogFragment;


public class BookmarksDialog extends SherlockDialogFragment {
	private String tags = "";
	private Bookmark bookmark;
	private TextView tvDate, tvHumanLink;
	private EditText tvName, tvTags;

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public static BookmarksDialog getInstance(Bookmark bookmark) {
		BookmarksDialog bmDial = new BookmarksDialog();
		bmDial.setBookmark(bookmark);
		return bmDial;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.bookmarks)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						addBookmarks();
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// null
					}
				});
		View customView = inflater.inflate(R.layout.bookmarks_dialog, null);
		builder.setView(customView);

		tvDate = (TextView) customView.findViewById(R.id.date);
		tvHumanLink = (TextView) customView.findViewById(R.id.humanLink);
		tvName = (EditText) customView.findViewById(R.id.bookmark_name);
		tvTags = (EditText) customView.findViewById(R.id.tags);

		fillField();

		return builder.create();
	}

	private void addBookmarks() {
		readField();
		new BookmarksManager(new dbBookmarksRepository()).add(bookmark, tags);
		Toast.makeText(getActivity(), getString(R.string.added), Toast.LENGTH_LONG).show();
	}

	private void fillField() {
		tvDate.setText(bookmark.date);
		tvHumanLink.setText(bookmark.humanLink);
		tvName.setText(bookmark.humanLink);
		tvTags.setText(this.tags);
	}

	private void readField() {
		bookmark.humanLink = tvHumanLink.getText().toString();
		bookmark.OSISLink = tvName.getText().toString();
		bookmark.date = tvDate.getText().toString();
		this.tags = tvTags.getText().toString();
	}
}
