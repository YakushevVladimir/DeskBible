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

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.dal.repository.bookmarks.BookmarksTags;
import com.BibleQuote.dal.repository.migration.Migration;
import com.BibleQuote.dal.repository.migration.Migration_1_2;
import com.BibleQuote.dal.repository.migration.Migration_2_3;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.utils.DataConstants;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: Vladimir Yakushev
 * Date: 02.05.13
 */
public final class DbLibraryHelper {

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
        File dbDir = new File(getDbDirPath());
        if (!dbDir.exists() && !dbDir.mkdir()) {
            dbDir = BibleQuoteApp.getInstance().getFilesDir();
        }
        File dbFile = new File(dbDir, DataConstants.getDbLibraryName());
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        if (db.getVersion() < VERSION) {
            db.beginTransaction();
            try {
                int oldVersion = db.getVersion();
                if (oldVersion == 0) {
                    onCreate(db);
                    oldVersion = 1;
                }
                onUpgrade(db, oldVersion, VERSION);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        return db;
    }

    @NonNull
    private static String getDbDirPath() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                ? DataConstants.getDbExternalDataPath()
                : DataConstants.getDbDataPath();
    }

    private static void onCreate(SQLiteDatabase db) {
        for (String command : CREATE_DATABASE) {
            db.execSQL(command);
        }
    }

    private static void onUpgrade(SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (oldVersion == newVersion) {
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

        db.setVersion(newVersion);
    }
}

