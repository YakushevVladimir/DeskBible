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

package com.BibleQuote.managers.bookmarks.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.BibleQuote.dal.dbLibraryHelper;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.utils.DataConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class dbBookmarksRepository implements IBookmarksRepository {
	private final static String TAG = dbBookmarksRepository.class.getSimpleName();
	private dbBookmarksTagsRepository bmTagRepo = new dbBookmarksTagsRepository();

	@Override
	public void sort() {
		Log.w(TAG, "Sort all bookmarks");
		SQLiteDatabase db = dbLibraryHelper.openDB();

		ArrayList<Bookmark> bookmarks = getAllRowsToArray(db);
		Collections.sort(bookmarks, new Comparator<Bookmark>() {
			@Override
			public int compare(Bookmark lhs, Bookmark rhs) {
				return rhs.OSISLink.compareTo(lhs.OSISLink);
			}
		});

		db.delete(DataConstants.BOOKMARKS_TABLE, null, null);

		for (Bookmark curr : bookmarks) {
			bmTagRepo.updateBookmarksID(db, curr.id, addRow(db, curr));
		}

		dbLibraryHelper.closeDB(db);
	}

	@Override
	public long add(Bookmark bookmark) {
		Log.w(TAG, String.format("Add bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));

		SQLiteDatabase db = dbLibraryHelper.openDB();
		long newID = addRow(db, bookmark);
		dbLibraryHelper.closeDB(db);

		return newID;
	}

	@Override
	public void delete(Bookmark bookmark) {
		Log.w(TAG, String.format("Delete bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));
		SQLiteDatabase db = dbLibraryHelper.openDB();
		db.delete(DataConstants.BOOKMARKS_TABLE, dbLibraryHelper.BOOKMARKS_OSIS + "=\"" + bookmark.OSISLink + "\"", null);
		bmTagRepo.deleteBookmarks(db, bookmark);
		dbLibraryHelper.closeDB(db);
	}

	@Override
	public void deleteAll() {
		Log.w(TAG, "Delete all bookmarks");
		SQLiteDatabase db = dbLibraryHelper.openDB();
		db.delete(DataConstants.BOOKMARKS_TABLE, null, null);
		dbLibraryHelper.closeDB(db);
		bmTagRepo.deleteAll();
	}

	@Override
	public ArrayList<Bookmark> getAll() {
		Log.w(TAG, "Get all bookmarks");
		SQLiteDatabase db = dbLibraryHelper.openDB();
		ArrayList<Bookmark> result = getAllRowsToArray(db);
		dbLibraryHelper.closeDB(db);
		return result;
	}

	@Override
	public ArrayList<Bookmark> getAll(Tag tag) {
		Log.w(TAG, "Get all bookmarks to tag: " + tag.name);
		SQLiteDatabase db = dbLibraryHelper.openDB();
		ArrayList<Bookmark> result = getAllRowsToArray(db, tag);
		dbLibraryHelper.closeDB(db);
		return result;
	}

	private long addRow(SQLiteDatabase db, Bookmark bookmark) {
		db.delete(DataConstants.BOOKMARKS_TABLE, dbLibraryHelper.BOOKMARKS_OSIS + "=\"" + bookmark.OSISLink + "\"", null);

		ContentValues values = new ContentValues();
		values.put(dbLibraryHelper.BOOKMARKS_LINK, bookmark.humanLink);
		values.put(dbLibraryHelper.BOOKMARKS_OSIS, bookmark.OSISLink);
		values.put(dbLibraryHelper.BOOKMARKS_DATE, bookmark.date);
		return db.insert(DataConstants.BOOKMARKS_TABLE, null, values);
	}

	private ArrayList<Bookmark> getAllRowsToArray(SQLiteDatabase db) {
		Cursor allRows = db.query(true, DataConstants.BOOKMARKS_TABLE,
				null, null, null, null, null, dbLibraryHelper.BOOKMARKS_KEY_ID + " DESC", null);
		return getBookmarks(allRows);
	}

	private ArrayList<Bookmark> getAllRowsToArray(SQLiteDatabase db, Tag tag) {
		Cursor allRows = db.rawQuery(
				"SELECT "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_KEY_ID + ", "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_OSIS + ", "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_OSIS + " "
				+ "FROM "
						+ DataConstants.BOOKMARKS_TABLE + ", " + DataConstants.BOOKMARKS_TAGS_TABLE + " "
				+ "WHERE "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_KEY_ID
							+ " = " + DataConstants.BOOKMARKS_TAGS_TABLE + "." + dbLibraryHelper.BOOKMARKS_TAGS_BM_ID
						+ " and "
							+ DataConstants.BOOKMARKS_TAGS_TABLE + "." + dbLibraryHelper.BOOKMARKS_TAGS_TAG_ID
							+ " = " + tag.id + " "
				+ "ORDER BY "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_KEY_ID + " DESC",
				null);
		return getBookmarks(allRows);
	}

	private ArrayList<Bookmark> getBookmarks(Cursor allRows) {
		ArrayList<Bookmark> result = new ArrayList<Bookmark>();
		if (allRows.moveToFirst()) {
			do {
				result.add(new Bookmark(
						allRows.getInt(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_KEY_ID)),
						allRows.getString(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_OSIS)),
						allRows.getString(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_LINK)),
						allRows.getString(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_DATE))
					)
				);
			} while (allRows.moveToNext());
		}
		return result;
	}

}
