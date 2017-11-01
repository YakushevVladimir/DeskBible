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
 * Created by Vladimir Yakushev at 11/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.bookmarks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.BibleQuote.dal.DbLibraryHelper;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.domain.repository.IBookmarksRepository;

import java.util.ArrayList;
import java.util.List;

public class DbBookmarksRepository implements IBookmarksRepository {

    private static final String TABLE_NAME = DbLibraryHelper.BOOKMARKS_TABLE;

    @Override
    public long add(Bookmark bookmark) {
        StaticLogger.info(this, String.format("Add bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));

        ContentValues values = new ContentValues();
        values.put(Bookmark.LINK, bookmark.humanLink);
        values.put(Bookmark.OSIS, bookmark.OSISLink);
        values.put(Bookmark.NAME, bookmark.name);
        values.put(Bookmark.DATE, bookmark.date);

        String query = String.format("SELECT * FROM %s WHERE %s=?", DbLibraryHelper.BOOKMARKS_TABLE, Bookmark.OSIS);
        String[] args = {bookmark.OSISLink};

        long result = -1;
        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        try (Cursor curr = db.rawQuery(query, args)) {
            db.beginTransaction();
            if (curr.moveToFirst()) {
                db.update(DbLibraryHelper.BOOKMARKS_TABLE, values, Bookmark.OSIS + "=?", args);
                result = curr.getLong(curr.getColumnIndex(Bookmark.KEY_ID));
            } else {
                result = db.insert(DbLibraryHelper.BOOKMARKS_TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            StaticLogger.error(this, "Add bookmark failed", ex);
        } finally {
            db.endTransaction();
            DbLibraryHelper.closeDB();
        }

        return result;
    }

    @Override
    public void delete(final Bookmark bookmark) {
        StaticLogger.info(this, String.format("Delete bookmarks %S:%s", bookmark.OSISLink, bookmark.humanLink));
        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, Bookmark.OSIS + "=?", new String[]{bookmark.OSISLink});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        DbLibraryHelper.closeDB();
    }

    @Override
    public List<Bookmark> getAll(@Nullable Tag tag) {
        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        List<Bookmark> result = new ArrayList<>();
        try {
            Cursor cursorB;
            if (tag != null) {
                String query = String.format("SELECT * FROM %1$s WHERE %4$s IN " +
                                "(SELECT %6$s FROM %3$s " +
                                "JOIN %2$s ON %2$s.%8$s=%3$s.%7$s AND %2$s.%5$s=?) ORDER BY %4$s DESC",
                        DbLibraryHelper.BOOKMARKS_TABLE, DbLibraryHelper.TAGS_TABLE,
                        DbLibraryHelper.BOOKMARKSTAGS_TABLE, Bookmark.KEY_ID, Tag.NAME,
                        BookmarksTags.BOOKMARKSTAGS_BM_ID, BookmarksTags.BOOKMARKSTAGS_TAG_ID,
                        Tag.KEY_ID);
                cursorB = db.rawQuery(query, new String[]{tag.name});
            } else {
                cursorB = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s DESC",
                        DbLibraryHelper.BOOKMARKS_TABLE, Bookmark.KEY_ID), null);
            }

            if (!cursorB.moveToFirst()) {
                cursorB.close();
                return result;
            }

            do {
                Bookmark bookmark = getBookmark(cursorB);
                bookmark.tags = getTags(db, bookmark.id);
                result.add(bookmark);
            } while (cursorB.moveToNext());

        } finally {
            DbLibraryHelper.closeDB();
        }
        return result;
    }

    @NonNull
    private String getTags(SQLiteDatabase db, long bookmarkIDs) {
        StringBuilder result = new StringBuilder();
        Cursor cur = db.rawQuery(String.format("SELECT %1$s FROM %2$s WHERE %3$s IN " +
                        "(SELECT %4$s FROM %5$s WHERE %6$s=?) ORDER BY %1$s",
                Tag.NAME, DbLibraryHelper.TAGS_TABLE, Tag.KEY_ID,
                BookmarksTags.BOOKMARKSTAGS_TAG_ID, DbLibraryHelper.BOOKMARKSTAGS_TABLE,
                BookmarksTags.BOOKMARKSTAGS_BM_ID),
                new String[]{String.valueOf(bookmarkIDs)});
        if (cur.moveToFirst()) {
            do {
                if (result.length() != 0) {
                    result.append(", ");
                }
                result.append(cur.getString(0));
            } while (cur.moveToNext());
        }
        cur.close();
        return result.toString();
    }

    @NonNull
    private Bookmark getBookmark(Cursor cursor) {
        return new Bookmark(
                cursor.getLong(cursor.getColumnIndex(Bookmark.KEY_ID)),
                cursor.getString(cursor.getColumnIndex(Bookmark.NAME)),
                cursor.getString(cursor.getColumnIndex(Bookmark.OSIS)),
                cursor.getString(cursor.getColumnIndex(Bookmark.LINK)),
                cursor.getString(cursor.getColumnIndex(Bookmark.DATE)));
    }
}
