/*
 * Copyright (C) 2011 Scripture Software
 *
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
 * Project: BibleQuote-for-Android
 * File: DbTagRepository.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.bookmarks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.BibleQuote.dal.DbLibraryHelper;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.repository.IBookmarksTagsRepository;
import com.BibleQuote.domain.repository.ITagRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DbTagRepository implements ITagRepository {

	private final static String TAG = DbTagRepository.class.getSimpleName();
	private IBookmarksTagsRepository bmTagRepo = new DbBookmarksTagsRepository();

	@Override
	public long add(String tag) {
		Log.w(TAG, String.format("Add tag %s", tag));
		SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
		long id = addRow(db, tag);
		DbLibraryHelper.closeDB();
		return id;
	}

	@Override
	public int update(Tag tag) {
		Log.w(TAG, String.format("Update tag %s", tag.name));
		SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		int result = -1;
		try {
			ContentValues values = new ContentValues();
			values.put(Tag.NAME, tag.name);

            String[] whereArgs = new String[1];
            whereArgs[0] = String.valueOf(tag.id);

			result = db.update(DbLibraryHelper.TAGS_TABLE, values, Tag.KEY_ID + "=?", whereArgs);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		DbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public int delete(Tag tag) {
		Log.w(TAG, String.format("Delete tag %s", tag.name));
		int result = -1;
		SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			bmTagRepo.deleteTag(db, tag);
            String[] whereArgs = new String[1];
            whereArgs[0] = String.valueOf(tag.id);

			result = db.delete(DbLibraryHelper.TAGS_TABLE, Tag.KEY_ID + "=?", whereArgs);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		DbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public ArrayList<Tag> getAll() {
		SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		ArrayList<Tag> result = new ArrayList<>();
		try {
			result.addAll(getAllRowsToArray(db));
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		DbLibraryHelper.closeDB();
		return result;
	}

	public LinkedHashMap<Tag, String> getAllWithCount() {
		SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		LinkedHashMap<Tag, String> result = new LinkedHashMap<>();
		try {
			Cursor allRows = db.rawQuery(
					"SELECT " + DbLibraryHelper.TAGS_TABLE + "." + Tag.KEY_ID + ", "
							+ DbLibraryHelper.TAGS_TABLE + "." + Tag.NAME + ", " +
							" COUNT(" + DbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_KEY_ID + ") AS count " +
							" FROM " + DbLibraryHelper.TAGS_TABLE + " " +
							" LEFT OUTER JOIN " + DbLibraryHelper.BOOKMARKSTAGS_TABLE +
							" ON " + DbLibraryHelper.TAGS_TABLE + "." + Tag.KEY_ID +
							" = " + DbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_TAG_ID +
							" GROUP BY " + DbLibraryHelper.TAGS_TABLE + "." + Tag.KEY_ID +
							" ORDER BY " + DbLibraryHelper.TAGS_TABLE + ".name", null);
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
		DbLibraryHelper.closeDB();

		return result;
	}

	@Override
	public int deleteAll() {
		SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		int result = -1;
		try {
			result = db.delete(DbLibraryHelper.TAGS_TABLE, null, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		DbLibraryHelper.closeDB();
		return result;
	}

	@Override
	public int deleteEmptyTags() {
		SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
		db.beginTransaction();
		try {
			db.execSQL(
					"DELETE FROM " + DbLibraryHelper.TAGS_TABLE
					+ " WHERE " + Tag.KEY_ID
							+ " IN (SELECT " + Tag.KEY_ID + " FROM " + DbLibraryHelper.TAGS_TABLE
						+ " WHERE NOT " + Tag.KEY_ID
							+ " IN (SELECT " + BookmarksTags.BOOKMARKSTAGS_TAG_ID + " FROM " + DbLibraryHelper.BOOKMARKSTAGS_TABLE + "))");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		DbLibraryHelper.closeDB();
		return 0;
	}

	private ArrayList<Tag> getAllRowsToArray(SQLiteDatabase db) {
		ArrayList<Tag> result = new ArrayList<>();
		Cursor allRows = db.query(true, DbLibraryHelper.TAGS_TABLE,
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
			Cursor cur = db.query(DbLibraryHelper.TAGS_TABLE, null, Tag.NAME + "=?", new String[]{tag}, null, null, null);
			if (cur.moveToFirst()) {
				result = cur.getInt(0);
				cur.close();
			} else {
				ContentValues values = new ContentValues();
				values.put(Tag.NAME, tag);
				result = db.insert(DbLibraryHelper.TAGS_TABLE, null, values);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return result;
	}

}
