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

	public static final String BOOKMARKS_KEY_ID = "_id";
	public static final String BOOKMARKS_OSIS = "osis";
	public static final String BOOKMARKS_LINK = "link";
	public static final String BOOKMARKS_NAME = "name";
	public static final String BOOKMARKS_DATE = "date";
	
	public static final String BOOKMARKS_TAGS_KEY_ID = "_id";
	public static final String BOOKMARKS_TAGS_BM_ID = "bm_id";
	public static final String BOOKMARKS_TAGS_TAG_ID = "tag_id";

	public static final String TAGS_KEY_ID = "_id";
	public static final String TAGS_NAME = "name";

	private static final String[] CREATE_DATABASE = new String[] {
			"create table " + DataConstants.BOOKMARKS_TABLE + " ("
					+ BOOKMARKS_KEY_ID + " integer primary key autoincrement, "
					+ BOOKMARKS_OSIS + " text unique not null, "
					+ BOOKMARKS_LINK + " text not null, "
					+ BOOKMARKS_NAME + " text not null, "
					+ BOOKMARKS_DATE + " text not null"
				+ ");",
			"create table " + DataConstants.BOOKMARKS_TAGS_TABLE + " ("
					+ BOOKMARKS_TAGS_KEY_ID + " integer primary key autoincrement, "
					+ BOOKMARKS_TAGS_BM_ID + " integer not null, "
					+ BOOKMARKS_TAGS_TAG_ID + " integer not null"
				+ ");",
			"create table " + DataConstants.TAGS_TABLE + " ("
					+ TAGS_KEY_ID + " integer primary key autoincrement, "
					+ TAGS_NAME + " text unique not null"
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
			db.execSQL("ALTER TABLE " + DataConstants.BOOKMARKS_TABLE + " ADD COLUMN " + BOOKMARKS_NAME + " TEXT;");
			db.setVersion(version);
		}
	}
}

