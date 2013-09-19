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
import com.BibleQuote.dal.dbLibraryHelper;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class dbBookmarksRepository implements IBookmarksRepository {
	private final static String TAG = dbBookmarksRepository.class.getSimpleName();
	private dbBookmarksTagsRepository bmTagRepo = new dbBookmarksTagsRepository();

	@Override
	public long add(Bookmark bookmark) {
		Log.i(TAG, String.format("Add bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));

		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		long newID;
		try {
			newID = addRow(db, bookmark);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();

		return newID;
	}

	@Override
	public void delete(final Bookmark bookmark) {
		Log.i(TAG, String.format("Delete bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			db.delete(DataConstants.BOOKMARKS_TABLE, dbLibraryHelper.BOOKMARKS_OSIS + "=\"" + bookmark.OSISLink + "\"", null);
			bmTagRepo.deleteBookmarks(db, bookmark);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
	}

	@Override
	public void deleteAll() {
		Log.i(TAG, "Delete all bookmarks");
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			db.delete(DataConstants.BOOKMARKS_TABLE, null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		bmTagRepo.deleteAll();
	}

	@Override
	public ArrayList<Bookmark> getAll() {
		Log.i(TAG, "Get all bookmarks");
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		ArrayList<Bookmark> result;
		try {
			result = getAllRowsToArray(db);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public ArrayList<Bookmark> getAll(Tag tag) {
		Log.i(TAG, "Get all bookmarks to tag: " + tag.name);
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		ArrayList<Bookmark> result;
		try {
			result = getAllRowsToArray(db, tag);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	private long addRow(SQLiteDatabase db, Bookmark bookmark) {
		db.delete(DataConstants.BOOKMARKS_TABLE, dbLibraryHelper.BOOKMARKS_OSIS + "=\"" + bookmark.OSISLink + "\"", null);

		ContentValues values = new ContentValues();
		values.put(dbLibraryHelper.BOOKMARKS_LINK, bookmark.humanLink);
		values.put(dbLibraryHelper.BOOKMARKS_OSIS, bookmark.OSISLink);
		values.put(dbLibraryHelper.BOOKMARKS_NAME, bookmark.name);
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
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_LINK + ", "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_NAME + ", "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_DATE + " "
				+ "FROM "
						+ DataConstants.BOOKMARKS_TABLE + ", " + DataConstants.BOOKMARKS_TAGS_TABLE + " "
				+ "WHERE "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_KEY_ID
							+ " = " + DataConstants.BOOKMARKS_TAGS_TABLE + "." + dbLibraryHelper.BOOKMARKS_TAGS_BM_ID
						+ " and "
							+ DataConstants.BOOKMARKS_TAGS_TABLE + "." + dbLibraryHelper.BOOKMARKS_TAGS_TAG_ID
							+ " = " + tag.id + " "
				+ "ORDER BY "
						+ DataConstants.BOOKMARKS_TABLE + "." + dbLibraryHelper.BOOKMARKS_KEY_ID + " DESC;",
				null);
		return getBookmarks(allRows);
	}

	private ArrayList<Bookmark> getBookmarks(Cursor allRows) {
		ArrayList<Bookmark> result = new ArrayList<Bookmark>();
		if (allRows.moveToFirst()) {
			do {
				Bookmark bm = new Bookmark(
						allRows.getInt(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_KEY_ID)),
						allRows.getString(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_OSIS)),
						allRows.getString(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_LINK)),
						allRows.getString(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_NAME)),
						allRows.getString(allRows.getColumnIndex(dbLibraryHelper.BOOKMARKS_DATE)));
				bm.tags = bmTagRepo.getTags(bm.id);
				result.add(bm);
			} while (allRows.moveToNext());
		}
		if(allRows != null && !allRows.isClosed()){
			allRows.close();
		}
		return result;
	}

}
