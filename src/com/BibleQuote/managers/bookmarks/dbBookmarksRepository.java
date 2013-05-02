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

package com.BibleQuote.managers.bookmarks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.BibleQuote.dal.LibraryDBHelper;
import com.BibleQuote.utils.DataConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class dbBookmarksRepository implements IBookmarksRepository {
	private final static String TAG = dbBookmarksRepository.class.getSimpleName();

	@Override
	public void sort() {
		Log.w(TAG, "Sort all bookmarks");
		SQLiteDatabase db = LibraryDBHelper.getLibraryDB();
		ArrayList<Bookmark> bookmarks = getAllRowsToArray(db);
		Collections.sort(bookmarks, new Comparator<Bookmark>() {
			@Override
			public int compare(Bookmark lhs, Bookmark rhs) {
				return lhs.OSISLink.compareTo(rhs.OSISLink);
			}
		});
		deleteAll();
		for (Bookmark curr : bookmarks) {
			addRow(db, curr);
		}
		db.close();
	}

	@Override
	public void add(Bookmark bookmark) {
		Log.w(TAG, String.format("Add bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));
		SQLiteDatabase db = LibraryDBHelper.getLibraryDB();
		addRow(db, bookmark);
		db.close();
	}

	@Override
	public void delete(Bookmark bookmark) {
		Log.w(TAG, String.format("Delete bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));
		SQLiteDatabase db = LibraryDBHelper.getLibraryDB();
		db.delete(DataConstants.BOOKMARKS_TABLE, LibraryDBHelper.BOOKMARKS_OSIS + "=\"" + bookmark.OSISLink + "\"", null);
		db.close();
	}

	@Override
	public void deleteAll() {
		Log.w(TAG, "Delete all bookmarks");
		SQLiteDatabase db = LibraryDBHelper.getLibraryDB();
		db.delete(DataConstants.BOOKMARKS_TABLE, null, null);
		db.close();
	}

	@Override
	public ArrayList<Bookmark> getAll() {
		Log.w(TAG, "Get all bookmarks");
		SQLiteDatabase db = LibraryDBHelper.getLibraryDB();
		ArrayList<Bookmark> result = getAllRowsToArray(db);
		db.close();
		return result;
	}

	private void addRow(SQLiteDatabase db, Bookmark bookmark) {
		db.delete(DataConstants.BOOKMARKS_TABLE, LibraryDBHelper.BOOKMARKS_OSIS + "=\"" + bookmark.OSISLink + "\"", null);

		ContentValues values = new ContentValues();
		values.put(LibraryDBHelper.BOOKMARKS_LINK, bookmark.humanLink);
		values.put(LibraryDBHelper.BOOKMARKS_OSIS, bookmark.OSISLink);
		db.insert(DataConstants.BOOKMARKS_TABLE, null, values);
	}

	private ArrayList<Bookmark> getAllRowsToArray(SQLiteDatabase db) {
		ArrayList<Bookmark> result = new ArrayList<Bookmark>();
		Cursor allRows = db.query(true, DataConstants.BOOKMARKS_TABLE,
				new String[]{LibraryDBHelper.BOOKMARKS_KEY_ID,
						LibraryDBHelper.BOOKMARKS_OSIS, LibraryDBHelper.BOOKMARKS_LINK},
				null, null, null, null, LibraryDBHelper.BOOKMARKS_KEY_ID + " DESC", null);

		if (allRows.moveToFirst()) {
			do {
				result.add(new Bookmark(
						allRows.getInt(allRows.getColumnIndex(LibraryDBHelper.BOOKMARKS_KEY_ID)),
						allRows.getString(allRows.getColumnIndex(LibraryDBHelper.BOOKMARKS_OSIS)),
						allRows.getString(allRows.getColumnIndex(LibraryDBHelper.BOOKMARKS_LINK))
				)
				);
			} while (allRows.moveToNext());
		}

		return result;
	}
}
