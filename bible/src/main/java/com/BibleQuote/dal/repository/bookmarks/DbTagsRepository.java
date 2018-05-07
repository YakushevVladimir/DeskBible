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
 * File: DbTagsRepository.java
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.bookmarks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.BibleQuote.dal.DbLibraryHelper;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.entity.TagWithCount;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.domain.repository.ITagsRepository;

import java.util.ArrayList;
import java.util.List;

public class DbTagsRepository implements ITagsRepository {

    @Override
    public void addTags(long bookmarkIDs, String tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }

        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        try {
            for (String value : tags.split(",")) {
                String tag = value.trim().toLowerCase(); // храним все теги записанные строчными буквами
                if (tag.isEmpty()) {
                    continue;
                }

                long tagIDs;
                Cursor cur = db.rawQuery(
                        String.format("SELECT * FROM %s WHERE %s=?", DbLibraryHelper.TAGS_TABLE, Tag.NAME),
                        new String[]{tag});
                if (cur.moveToFirst()) { // тег существует, берем его идентификатор
                    tagIDs = cur.getLong(cur.getColumnIndex(Tag.KEY_ID));
                } else { // записываем новый тег
                    ContentValues values = new ContentValues(1);
                    values.put(Tag.NAME, tag);
                    tagIDs = db.insert(DbLibraryHelper.TAGS_TABLE, null, values);
                }
                cur.close();

                // привязываем тег к закладке
                ContentValues values = new ContentValues(2);
                values.put(BookmarksTags.BOOKMARKSTAGS_BM_ID, bookmarkIDs);
                values.put(BookmarksTags.BOOKMARKSTAGS_TAG_ID, tagIDs);
                db.insert(DbLibraryHelper.BOOKMARKS_TAGS_TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            StaticLogger.error(this, "Failed add tags: " + tags, ex);
        } finally {
            db.endTransaction();
            DbLibraryHelper.closeDB();
        }
    }

    @Override
    public boolean deleteTag(String tag) {
        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        try {
            db.execSQL(String.format("DELETE FROM %1$s WHERE %2$s=?",
                    DbLibraryHelper.TAGS_TABLE, Tag.NAME),
                    new String[]{tag});
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            StaticLogger.error(this, "Failed delete tag: " + tag, ex);
            return false;
        } finally {
            db.endTransaction();
            DbLibraryHelper.closeDB();
        }
        return true;
    }

    @Override
    public List<TagWithCount> getTagsWithCount() {
        SQLiteDatabase db = DbLibraryHelper.getLibraryDB();
        db.beginTransaction();
        List<TagWithCount> result = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery(
                    String.format(
                            "SELECT %1$s.%3$s, %1$s.%4$s, COUNT(%2$s.%5$s) AS count FROM %1$s " +
                                    "LEFT JOIN %2$s ON %1$s.%3$s = %2$s.%5$s GROUP BY %2$s.%5$s ORDER BY %1$s.%4$s",
                            DbLibraryHelper.TAGS_TABLE, DbLibraryHelper.BOOKMARKS_TAGS_TABLE,
                            Tag.KEY_ID, Tag.NAME, BookmarksTags.BOOKMARKSTAGS_TAG_ID), null);
            db.setTransactionSuccessful();
            if (cursor.moveToFirst()) {
                do {
                    result.add(TagWithCount.create(
                            new Tag(cursor.getInt(0), cursor.getString(1)),
                            cursor.getString(2)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } finally {
            db.endTransaction();
        }
        DbLibraryHelper.closeDB();

        return result;
    }
}
