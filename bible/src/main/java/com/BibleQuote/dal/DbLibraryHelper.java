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
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.BibleQuote.dal.repository.bookmarks.BookmarksTags;
import com.BibleQuote.dal.repository.migration.Migration;
import com.BibleQuote.dal.repository.migration.Migration_1_2;
import com.BibleQuote.dal.repository.migration.Migration_2_3;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.FsUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Yakushev V.V. / ru.phoenix@gmail.com
 * @since 02.05.13
 */
public final class DbLibraryHelper {

    public static final String DB_NAME = "library.db";
    public static final String TAGS_TABLE = "tags";
    public static final String BOOKMARKS_TAGS_TABLE = "bookmarks_tags";
    public static final String BOOKMARKS_TABLE = "bookmarks";

    private static final String[] CREATE_DATABASE = new String[]{
            "create table " + BOOKMARKS_TABLE + " ("
                    + Bookmark.KEY_ID + " integer primary key autoincrement, "
                    + Bookmark.OSIS + " text unique not null, "
                    + Bookmark.LINK + " text not null, "
                    + Bookmark.DATE + " text not null"
                    + ");",
            "create table " + BOOKMARKS_TAGS_TABLE + " ("
                    + BookmarksTags.BOOKMARKSTAGS_KEY_ID + " integer primary key autoincrement, "
                    + BookmarksTags.BOOKMARKSTAGS_BM_ID + " integer not null, "
                    + BookmarksTags.BOOKMARKSTAGS_TAG_ID + " integer not null"
                    + ");",
            "create table " + TAGS_TABLE + " ("
                    + Tag.KEY_ID + " integer primary key autoincrement, "
                    + Tag.NAME + " text unique not null"
                    + ");"
    };
    private static final List<Migration> MIGRATIONS = Arrays.asList(new Migration_1_2(), new Migration_2_3());
    private static final int VERSION = 3;

    private final Context appContext;
    private SQLiteDatabase database;

    public DbLibraryHelper(Context context) {
        appContext = context.getApplicationContext();
    }

    public SQLiteDatabase getDatabase() {
        if (database == null) {
            database = openOrCreateDatabase();
        }
        return database;
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
        database = null;
    }

    private File getDatabaseFile() {
        File dbExternalDir = new File(DataConstants.getDbExternalDataPath());
        File dbFile = FsUtils.findFile(DB_NAME,
                dbExternalDir,
                new File(DataConstants.getDbDataPath()),
                appContext.getFilesDir());
        if (dbFile == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (!dbExternalDir.exists() && !dbExternalDir.mkdir()) {
                    dbFile = appContext.getDatabasePath(DB_NAME);
                } else {
                    dbFile = new File(dbExternalDir, DB_NAME);
                }
            }
        }
        return dbFile;
    }

    private void onCreate(SQLiteDatabase db) {
        for (String command : CREATE_DATABASE) {
            db.execSQL(command);
        }
    }

    private void onUpgrade(SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (oldVersion == newVersion) {
            // миграция не требуется
            return;
        }

        Collections.sort(MIGRATIONS);
        int currVersion = oldVersion;
        for (Migration migration : MIGRATIONS) {
            // ищем миграцию для текущей версии БД
            if (migration.oldVersion != currVersion) {
                continue;
            }

            // выполняем миграцию и обновляем текущую версию
            migration.migrate(db);
            currVersion = migration.newVersion;

            // если достигли требуемой версии БД, то прерываем миграцию
            if (currVersion == newVersion) {
                break;
            }
        }
    }

    private SQLiteDatabase openOrCreateDatabase() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDatabaseFile(), null);
        int oldVersion = db.getVersion();
        if (oldVersion < VERSION) {
            db.beginTransaction();
            try {
                if (oldVersion == 0) {
                    onCreate(db);
                    oldVersion = 1;
                }
                onUpgrade(db, oldVersion, VERSION);
                db.setVersion(VERSION);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        return db;
    }
}

