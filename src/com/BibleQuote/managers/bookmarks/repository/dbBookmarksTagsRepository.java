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

/**
 * User: Vladimir Yakushev
 * Date: 13.05.13
 */
public class dbBookmarksTagsRepository {
	private final static String TAG = dbBookmarksTagsRepository.class.getSimpleName();

	public void add(long bmID, ArrayList<Long> tagIDs){
		SQLiteDatabase db = dbLibraryHelper.openDB();
		for (long tagID : tagIDs) {
			ContentValues values = new ContentValues();
			values.put(dbLibraryHelper.BOOKMARKS_TAGS_BM_ID, bmID);
			values.put(dbLibraryHelper.BOOKMARKS_TAGS_TAG_ID, tagID);
			db.insert(DataConstants.BOOKMARKS_TAGS_TABLE, null, values);
		}
		dbLibraryHelper.closeDB(db);
	}

	public void deleteAll() {
		Log.w(TAG, "Delete all bookmarks");
		SQLiteDatabase db = dbLibraryHelper.openDB();
		db.delete(DataConstants.BOOKMARKS_TAGS_TABLE, null, null);
		dbLibraryHelper.closeDB(db);
	}

	public void updateBookmarksID(SQLiteDatabase db, long oldID, long newID) {
		ContentValues values = new ContentValues();
		values.put(dbLibraryHelper.BOOKMARKS_TAGS_BM_ID, newID);
		db.update(DataConstants.BOOKMARKS_TAGS_TABLE, values, dbLibraryHelper.BOOKMARKS_TAGS_BM_ID + "=" + oldID, null);
	}

	public void deleteBookmarks(SQLiteDatabase db, Bookmark bm) {
		db.delete(DataConstants.BOOKMARKS_TAGS_TABLE, dbLibraryHelper.BOOKMARKS_TAGS_BM_ID + "=" + bm.id, null);
	}

	public void deleteTag(SQLiteDatabase db, Tag tag) {
		db.delete(DataConstants.BOOKMARKS_TAGS_TABLE, dbLibraryHelper.BOOKMARKS_TAGS_TAG_ID + "=" + tag.id, null);
	}
}
