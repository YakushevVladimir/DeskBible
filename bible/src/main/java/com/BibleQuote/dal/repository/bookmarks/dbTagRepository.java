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
 * File: dbTagRepository.java
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
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.repository.IBookmarksTagsRepository;
import com.BibleQuote.domain.repository.ITagRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class dbTagRepository implements ITagRepository {
	private final static String TAG = dbTagRepository.class.getSimpleName();
	private IBookmarksTagsRepository bmTagRepo = new dbBookmarksTagsRepository();

	@Override
	public long add(String tag) {
		Log.w(TAG, String.format("Add tag %s", tag));
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		long id = addRow(db, tag);
		dbLibraryHelper.closeDB();
		return id;
	}

	@Override
	public int update(Tag tag) {
		Log.w(TAG, String.format("Update tag %s", tag.name));
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		int result = -1;
		try {
			ContentValues values = new ContentValues();
			values.put(Tag.NAME, tag.name);

            String[] whereArgs = new String[1];
            whereArgs[0] = String.valueOf(tag.id);

            result = db.update(dbLibraryHelper.TAGS_TABLE, values, Tag.KEY_ID + "=?", whereArgs);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public int delete(Tag tag) {
		Log.w(TAG, String.format("Delete tag %s", tag.name));
		int result = -1;
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			bmTagRepo.deleteTag(db, tag);
            String[] whereArgs = new String[1];
            whereArgs[0] = String.valueOf(tag.id);

            result = db.delete(dbLibraryHelper.TAGS_TABLE, Tag.KEY_ID + "=?", whereArgs);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public ArrayList<Tag> getAll() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		ArrayList<Tag> result = new ArrayList<Tag>();
		try {
			result = getAllRowsToArray(db);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	public LinkedHashMap<Tag, String> getAllWithCount() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		LinkedHashMap<Tag, String> result = new LinkedHashMap<Tag, String>();
		try {
			Cursor allRows = db.rawQuery(
					"SELECT " + dbLibraryHelper.TAGS_TABLE + "." + Tag.KEY_ID + ", "
							+ dbLibraryHelper.TAGS_TABLE + "." + Tag.NAME + ", " +
							" COUNT(" + dbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_KEY_ID + ") AS count " +
					" FROM " + dbLibraryHelper.TAGS_TABLE + " " +
					" LEFT OUTER JOIN " + dbLibraryHelper.BOOKMARKSTAGS_TABLE +
							" ON " + dbLibraryHelper.TAGS_TABLE + "." + Tag.KEY_ID +
								" = " + dbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_TAG_ID +
					" GROUP BY " + dbLibraryHelper.TAGS_TABLE + "." +  Tag.KEY_ID +
							" ORDER BY " + dbLibraryHelper.TAGS_TABLE + ".name", null);
			db.setTransactionSuccessful();
			if (allRows.moveToFirst()) {
				do {
					result.put(new Tag(allRows.getInt(0), allRows.getString(1)), allRows.getString(2));
				} while (allRows.moveToNext());
			}
			allRows.close();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();

		return result;
	}

	@Override
	public int deleteAll() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		int result = -1;
		try {
			result = db.delete(dbLibraryHelper.TAGS_TABLE, null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public int deleteEmptyTags() {
		SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			db.execSQL(
				"DELETE FROM " + dbLibraryHelper.TAGS_TABLE
					+ " WHERE " + Tag.KEY_ID
					+ " IN (SELECT " + Tag.KEY_ID + " FROM " + dbLibraryHelper.TAGS_TABLE
						+ " WHERE NOT " + Tag.KEY_ID
						+ " IN (SELECT " + BookmarksTags.BOOKMARKSTAGS_TAG_ID + " FROM " + dbLibraryHelper.BOOKMARKSTAGS_TABLE + "))");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		dbLibraryHelper.closeDB();
		return 0;
	}

	private ArrayList<Tag> getAllRowsToArray(SQLiteDatabase db) {
		ArrayList<Tag> result = new ArrayList<Tag>();
		Cursor allRows = db.query(true, dbLibraryHelper.TAGS_TABLE,
				null, null, null, null, null, Tag.NAME, null);
		if (allRows.moveToFirst()) {
			do {
				result.add(new Tag(
						allRows.getInt(allRows.getColumnIndex(Tag.KEY_ID)),
						allRows.getString(allRows.getColumnIndex(Tag.NAME))
				)
				);
			} while (allRows.moveToNext());
		}
		allRows.close();
		return result;
	}

	private long addRow(SQLiteDatabase db, String tag) {
		long result;
		db.beginTransaction();
		try {
			Cursor cur = db.query(dbLibraryHelper.TAGS_TABLE, null, Tag.NAME + "=?", new String[] {tag}, null, null, null);
			if (cur.moveToFirst()) {
				result = cur.getInt(0);
				cur.close();
			} else {
				ContentValues values = new ContentValues();
				values.put(Tag.NAME, tag);
				result = db.insert(dbLibraryHelper.TAGS_TABLE, null, values);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return result;
	}

}
