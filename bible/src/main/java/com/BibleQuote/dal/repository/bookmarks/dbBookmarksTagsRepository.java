/*
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
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: dbBookmarksTagsRepository.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.repository.bookmarks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.BibleQuote.dal.dbLibraryHelper;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.repository.IBookmarksTagsRepository;

import java.util.ArrayList;

/**
 * User: Vladimir Yakushev
 * Date: 13.05.13
 */
public class dbBookmarksTagsRepository implements IBookmarksTagsRepository {
	private final static String TAG = dbBookmarksTagsRepository.class.getSimpleName();

	@Override
	public void add(long bmID, ArrayList<Long> tagIDs){
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			db.delete(dbLibraryHelper.BOOKMARKSTAGS_TABLE, BookmarksTags.BOOKMARKSTAGS_BM_ID + " = \"" + bmID + "\"", null);
			for (long tagID : tagIDs) {
				ContentValues values = new ContentValues();
				values.put(BookmarksTags.BOOKMARKSTAGS_BM_ID, bmID);
				values.put(BookmarksTags.BOOKMARKSTAGS_TAG_ID, tagID);
				db.insert(dbLibraryHelper.BOOKMARKSTAGS_TABLE, null, values);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
	}

	@Override
	public void deleteAll() {
		Log.w(TAG, "Delete all bookmarks");
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			db.delete(dbLibraryHelper.BOOKMARKSTAGS_TABLE, null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
	}

	@Override
	public void deleteBookmarks(SQLiteDatabase db, Bookmark bm) {
		db.delete(dbLibraryHelper.BOOKMARKSTAGS_TABLE, BookmarksTags.BOOKMARKSTAGS_BM_ID + "=" + bm.id, null);
	}

	@Override
	public void deleteTag(SQLiteDatabase db, Tag tag) {
		db.delete(dbLibraryHelper.BOOKMARKSTAGS_TABLE, BookmarksTags.BOOKMARKSTAGS_TAG_ID + "=" + tag.id, null);
	}

	@Override
	public String getTags(long bmID) {
		StringBuilder result = new StringBuilder();
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			Cursor cur = db.rawQuery("SELECT " + Tag.NAME
					+ " FROM " + dbLibraryHelper.TAGS_TABLE + ", " + dbLibraryHelper.BOOKMARKSTAGS_TABLE
					+ " WHERE " + dbLibraryHelper.TAGS_TABLE + "." + Tag.KEY_ID
					+ " = " + dbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_TAG_ID
					+ " AND " + dbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_BM_ID
					+ " = \"" + bmID + "\";",
					null);
			if (cur.moveToFirst()) {
				do {
					if (result.length() != 0)
						result.append(", ");
					result.append(cur.getString(0));
				} while (cur.moveToNext());
			}
			cur.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return result.toString();
	}
}
