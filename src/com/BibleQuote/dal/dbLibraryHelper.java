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

package com.BibleQuote.dal;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.managers.bookmarks.BookmarksTags;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.Log;

import java.io.File;

/**
 * User: Vladimir Yakushev
 * Date: 02.05.13
 */
public class dbLibraryHelper {
	private final static String TAG = dbLibraryHelper.class.getSimpleName();

	private static int version = 2;
	private static SQLiteDatabase db;

	public static final String TAGS_TABLE = "tags";
	public static final String BOOKMARKSTAGS_TABLE = "bookmarks_tags";
	public static final String BOOKMARKS_TABLE = "bookmarks";

	private static final String[] CREATE_DATABASE = new String[] {
			"create table " + BOOKMARKS_TABLE + " ("
					+ Bookmark.KEY_ID + " integer primary key autoincrement, "
					+ Bookmark.OSIS + " text unique not null, "
					+ Bookmark.LINK + " text not null, "
					+ Bookmark.NAME + " text not null, "
					+ Bookmark.DATE + " text not null"
				+ ");",
			"create table " + BOOKMARKSTAGS_TABLE + " ("
					+ BookmarksTags.BOOKMARKSTAGS_KEY_ID + " integer primary key autoincrement, "
					+ BookmarksTags.BOOKMARKSTAGS_BM_ID + " integer not null, "
					+ BookmarksTags.BOOKMARKSTAGS_TAG_ID + " integer not null"
				+ ");",
			"create table " + TAGS_TABLE + " ("
					+ Tag.KEY_ID + " integer primary key autoincrement, "
					+ Tag.NAME + " text unique not null"
				+ ");"
	};

	private static final String DB_DIR_PATH = (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
			? DataConstants.DB_EXTERNAL_DATA_PATH
			: DataConstants.DB_DATA_PATH);

	private static SQLiteDatabase getDB() {
		File dbDir = new File(DB_DIR_PATH);
		if (!dbDir.exists()) dbDir.mkdir();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(new File(dbDir, DataConstants.DB_LIBRARY_NAME), null);

		if (db.getVersion() != version) {
			db.beginTransaction();
			try {
				int currVersion = db.getVersion();
				if (currVersion == 0) {
					onCreate(db);
				};
				onUpgrade(db, currVersion);
				db.setVersion(version);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}

		return db;
	}

	public static SQLiteDatabase getLibraryDB() {
		if (db == null) {
			db = getDB();
		}
		return db;
	}

	public static void closeDB() {
		db.close();
		db = null;
	}

	private static void onCreate(SQLiteDatabase db) {
		for (String command : CREATE_DATABASE) {
			db.execSQL(command);
		}
	}

	private static void onUpgrade(SQLiteDatabase db, int currVersion) {
		if (currVersion == 1 && version == 2) {
			Log.i(TAG, "Upgrade DB to version 2");
			db.execSQL("ALTER TABLE " + BOOKMARKS_TABLE + " ADD COLUMN " + Bookmark.NAME + " TEXT;");
			db.setVersion(version);
		}
	}
}

