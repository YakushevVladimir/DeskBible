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
 * File: DB_BookmarksRepository.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.bookmarks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.BibleQuote.dal.dbLibraryHelper;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.domain.repository.IBookmarksTagsRepository;
import com.BibleQuote.utils.Logger;

import java.util.ArrayList;

public class DB_BookmarksRepository implements IBookmarksRepository {
    private final static String TAG = DB_BookmarksRepository.class.getSimpleName();
    private IBookmarksTagsRepository bmTagRepo = new dbBookmarksTagsRepository();

    @Override
    public long add(Bookmark bookmark) {
        Logger.i(TAG, String.format("Add bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));

        SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        long newID;
        try {
            newID = addRow(db, bookmark);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbLibraryHelper.closeDB();

        return newID;
    }

    @Override
    public void delete(final Bookmark bookmark) {
        Logger.i(TAG, String.format("Delete bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));
        SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        try {
            db.delete(dbLibraryHelper.BOOKMARKS_TABLE, Bookmark.OSIS + "=\"" + bookmark.OSISLink + "\"", null);
            bmTagRepo.deleteBookmarks(db, bookmark);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbLibraryHelper.closeDB();
    }

    @Override
    public void deleteAll() {
        Logger.i(TAG, "Delete all bookmarks");
        SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        try {
            db.delete(dbLibraryHelper.BOOKMARKS_TABLE, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbLibraryHelper.closeDB();
        bmTagRepo.deleteAll();
    }

    @Override
    public ArrayList<Bookmark> getAll() {
        Logger.i(TAG, "Get all bookmarks");
        SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        ArrayList<Bookmark> result;
        try {
            result = getAllRowsToArray(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbLibraryHelper.closeDB();
        return result;
    }

    @Override
    public ArrayList<Bookmark> getAll(Tag tag) {
        Logger.i(TAG, "Get all bookmarks to tag: " + tag.name);
        SQLiteDatabase db = dbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        ArrayList<Bookmark> result;
        try {
            result = getAllRowsToArray(db, tag);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbLibraryHelper.closeDB();
        return result;
    }

    private long addRow(SQLiteDatabase db, Bookmark bookmark) {
        ContentValues values = new ContentValues();
        values.put(Bookmark.LINK, bookmark.humanLink);
        values.put(Bookmark.OSIS, bookmark.OSISLink);
        values.put(Bookmark.NAME, bookmark.name);
        values.put(Bookmark.DATE, bookmark.date);

        Cursor curr = db.query(true, dbLibraryHelper.BOOKMARKS_TABLE,
                null, Bookmark.OSIS + " = \"" + bookmark.OSISLink + "\"", null, null, null, null, null);
        if (curr.moveToFirst()) {
            bookmark.id = curr.getInt(curr.getColumnIndex(Bookmark.KEY_ID));
            values.put(Bookmark.KEY_ID, bookmark.id);
            db.update(dbLibraryHelper.BOOKMARKS_TABLE, values, Bookmark.KEY_ID + " = \"" + bookmark.id + "\"", null);
            curr.close();
            return bookmark.id;
        } else {
            return db.insert(dbLibraryHelper.BOOKMARKS_TABLE, null, values);
        }
    }

    private ArrayList<Bookmark> getAllRowsToArray(SQLiteDatabase db) {
        Cursor allRows = db.query(true, dbLibraryHelper.BOOKMARKS_TABLE,
                null, null, null, null, null, Bookmark.KEY_ID + " DESC", null);
        return getBookmarks(allRows);
    }

    private ArrayList<Bookmark> getAllRowsToArray(SQLiteDatabase db, Tag tag) {
        Cursor allRows = db.rawQuery(
                "SELECT "
                        + dbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.KEY_ID + ", "
                        + dbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.OSIS + ", "
                        + dbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.LINK + ", "
                        + dbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.NAME + ", "
                        + dbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.DATE + " "
                        + "FROM "
                        + dbLibraryHelper.BOOKMARKS_TABLE + ", " + dbLibraryHelper.BOOKMARKSTAGS_TABLE + " "
                        + "WHERE "
                        + dbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.KEY_ID
                        + " = " + dbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_BM_ID
                        + " and "
                        + dbLibraryHelper.BOOKMARKSTAGS_TABLE + "." + BookmarksTags.BOOKMARKSTAGS_TAG_ID
                        + " = " + tag.id + " "
                        + "ORDER BY "
                        + dbLibraryHelper.BOOKMARKS_TABLE + "." + Bookmark.KEY_ID + " DESC;",
                null);
        return getBookmarks(allRows);
    }

    private ArrayList<Bookmark> getBookmarks(Cursor allRows) {
        ArrayList<Bookmark> result = new ArrayList<Bookmark>();
        if (allRows.moveToFirst()) {
            do {
                Bookmark bm = new Bookmark(
                        allRows.getInt(allRows.getColumnIndex(Bookmark.KEY_ID)),
                        allRows.getString(allRows.getColumnIndex(Bookmark.OSIS)),
                        allRows.getString(allRows.getColumnIndex(Bookmark.LINK)),
                        allRows.getString(allRows.getColumnIndex(Bookmark.NAME)),
                        allRows.getString(allRows.getColumnIndex(Bookmark.DATE)));
                bm.tags = bmTagRepo.getTags(bm.id);
                result.add(bm);
            } while (allRows.moveToNext());
        }
        allRows.close();
        return result;
    }

}
