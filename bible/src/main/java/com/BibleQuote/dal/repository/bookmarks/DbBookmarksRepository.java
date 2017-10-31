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
 * File: DbBookmarksRepository.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.bookmarks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.BibleQuote.dal.DbLibraryHelper;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.domain.repository.IBookmarksTagsRepository;

import java.util.ArrayList;

public class DbBookmarksRepository implements IBookmarksRepository {
    
    private IBookmarksTagsRepository bmTagRepo = new DbBookmarksTagsRepository();

    @Override
    public long add(Bookmark bookmark) {
        StaticLogger.info(this, String.format("Add bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));

        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        long newID;
        try {
            newID = addRow(db, bookmark);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        DbLibraryHelper.closeDB();

        return newID;
    }

    @Override
    public void delete(final Bookmark bookmark) {
        StaticLogger.info(this, String.format("Delete bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));
        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        try {
            db.delete(DbLibraryHelper.BOOKMARKS_TABLE, Bookmark.OSIS + "=\"" + bookmark.OSISLink + "\"", null);
            bmTagRepo.deleteBookmarks(db, bookmark);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        DbLibraryHelper.closeDB();
    }

    @Override
    public void deleteAll() {
        StaticLogger.info(this, "Delete all bookmarks");
        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        try {
            db.delete(DbLibraryHelper.BOOKMARKS_TABLE, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        DbLibraryHelper.closeDB();
        bmTagRepo.deleteAll();
    }

    @Override
    public ArrayList<Bookmark> getAll(@Nullable Tag tag) {
        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        ArrayList<Bookmark> result;
        try {
            if (tag != null) {
                result = getAllRowsToArray(db, tag);
            } else {
                result = getAllRowsToArray(db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        DbLibraryHelper.closeDB();
        return result;
    }

    private long addRow(SQLiteDatabase db, Bookmark bookmark) {
        ContentValues values = new ContentValues();
        values.put(Bookmark.LINK, bookmark.humanLink);
        values.put(Bookmark.OSIS, bookmark.OSISLink);
        values.put(Bookmark.NAME, bookmark.name);
        values.put(Bookmark.DATE, bookmark.date);

        Cursor curr = db.query(true, DbLibraryHelper.BOOKMARKS_TABLE,
                null, Bookmark.OSIS + " = \"" + bookmark.OSISLink + "\"", null, null, null, null, null);
        if (curr.moveToFirst()) {
            bookmark.id = curr.getLong(curr.getColumnIndex(Bookmark.KEY_ID));
            values.put(Bookmark.KEY_ID, bookmark.id);
            db.update(DbLibraryHelper.BOOKMARKS_TABLE, values, Bookmark.KEY_ID + " = \"" + bookmark.id + "\"", null);
            curr.close();
            return bookmark.id;
        } else {
            return db.insert(DbLibraryHelper.BOOKMARKS_TABLE, null, values);
        }
    }

    private ArrayList<Bookmark> getAllRowsToArray(SQLiteDatabase db) {
        StaticLogger.info(this, "Get all bookmarks");
        Cursor allRows = db.query(true, DbLibraryHelper.BOOKMARKS_TABLE,
                null, null, null, null, null, Bookmark.KEY_ID + " DESC", null);
        return getBookmarks(allRows);
    }

    private ArrayList<Bookmark> getAllRowsToArray(SQLiteDatabase db, Tag tag) {
        StaticLogger.info(this, "Get all bookmarks to tag: " + tag.name);
        Cursor allRows = db.rawQuery(
                "SELECT "
                        + DbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.KEY_ID + ", "
                        + DbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.OSIS + ", "
                        + DbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.LINK + ", "
                        + DbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.NAME + ", "
                        + DbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.DATE + " "
                        + "FROM "
                        + DbLibraryHelper.BOOKMARKS_TABLE + ", " + DbLibraryHelper.BOOKMARKSTAGS_TABLE + " "
                        + "WHERE "
                        + DbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.KEY_ID
                        + " = " + DbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_BM_ID
                        + " and "
                        + DbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_TAG_ID
                        + " = " + tag.id + " "
                        + "ORDER BY "
                        + DbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.KEY_ID + " DESC;",
                null);
        return getBookmarks(allRows);
    }

    private ArrayList<Bookmark> getBookmarks(Cursor allRows) {
        ArrayList<Bookmark> result = new ArrayList<>();
        if (allRows.moveToFirst()) {
            do {
                Bookmark bm = new Bookmark(
                        allRows.getLong(allRows.getColumnIndex(Bookmark.KEY_ID)),
                        allRows.getString(allRows.getColumnIndex(Bookmark.NAME)), allRows.getString(allRows.getColumnIndex(Bookmark.OSIS)),
                        allRows.getString(allRows.getColumnIndex(Bookmark.LINK)),
                        allRows.getString(allRows.getColumnIndex(Bookmark.DATE)));
                bm.tags = bmTagRepo.getTags(bm.id);
                result.add(bm);
            } while (allRows.moveToNext());
        }
        allRows.close();
        return result;
    }

}
