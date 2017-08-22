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
 * File: DbLibraryHelper.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.dal.repository.bookmarks.BookmarksTags;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.Logger;

import java.io.File;

/**
 * User: Vladimir Yakushev
 * Date: 02.05.13
 */
public final class DbLibraryHelper {

    public static final String TAGS_TABLE = "tags";
    public static final String BOOKMARKSTAGS_TABLE = "bookmarks_tags";
    public static final String BOOKMARKS_TABLE = "bookmarks";
    private static final String[] CREATE_DATABASE = new String[]{
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
    private static final String DB_DIR_PATH = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
            ? DataConstants.DB_EXTERNAL_DATA_PATH
            : DataConstants.DB_DATA_PATH;
    private final static String TAG = DbLibraryHelper.class.getSimpleName();
    private static int version = 2;
    private static SQLiteDatabase db;

    private DbLibraryHelper() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static void closeDB() {
        db.close();
        db = null;
    }

    public static SQLiteDatabase getLibraryDB() {
        if (db == null) {
            db = getDB();
        }
        return db;
    }

    private static SQLiteDatabase getDB() {
        File dbDir = new File(DB_DIR_PATH);
        if (!dbDir.exists() && !dbDir.mkdir()) {
            dbDir = BibleQuoteApp.getInstance().getFilesDir();
        }
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(new File(dbDir, DataConstants.DB_LIBRARY_NAME), null);

        if (db.getVersion() != version) {
            db.beginTransaction();
            try {
                int currVersion = db.getVersion();
                if (currVersion == 0) {
                    onCreate(db);
                }
                onUpgrade(db, currVersion);
                db.setVersion(version);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        return db;
    }

    private static void onCreate(SQLiteDatabase db) {
        for (String command : CREATE_DATABASE) {
            db.execSQL(command);
        }
    }

    private static void onUpgrade(SQLiteDatabase db, int currVersion) {
        if (currVersion == 1 && version == 2) {
            Logger.i(TAG, "Upgrade DB to version 2");
            db.execSQL("ALTER TABLE " + BOOKMARKS_TABLE + " ADD COLUMN " + Bookmark.NAME + " TEXT;");
            db.setVersion(version);
        }
    }
}

