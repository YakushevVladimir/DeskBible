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

import java.io.File;

/**
 * User: Vladimir Yakushev
 * Date: 02.05.13
 */
public class dbLibraryHelper {
	private final static String TAG = dbLibraryHelper.class.getSimpleName();

	private static int version = 1;

	public static final String BOOKMARKS_KEY_ID = "_id";
	public static final String BOOKMARKS_OSIS = "osis";
	public static final String BOOKMARKS_LINK = "link";
	public static final String BOOKMARKS_DATE = "date";

	private static final String CREATE_DATABASE =
			"create table " + DataConstants.BOOKMARKS_TABLE + " ("
					+ BOOKMARKS_KEY_ID + " integer primary key autoincrement, "
					+ BOOKMARKS_OSIS + " text not null, "
					+ BOOKMARKS_LINK + " text not null, "
					+ BOOKMARKS_DATE + " text not null"
					+ ");";

	private static final String DB_DIR_PATH = (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
			? DataConstants.DB_EXTERNAL_DATA_PATH
			: DataConstants.DB_DATA_PATH);

	public static SQLiteDatabase getLibraryDB() {
		File dbDir = new File(DB_DIR_PATH);
		if (!dbDir.exists()) dbDir.mkdir();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(new File(dbDir, DataConstants.DB_LIBRARY_NAME), null);

		if (db.getVersion() != version) {
			db.beginTransaction();
			try {
				if (db.getVersion() == 0) {
					onCreate(db);
				} else {
					onUpgrade(db);
				}
				db.setVersion(version);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}

		return db;
	}

	private static void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE);
	}

	private static void onUpgrade(SQLiteDatabase db) {
		//TODO Create dbLibraryHelper.onUpgrade()
	}

}

