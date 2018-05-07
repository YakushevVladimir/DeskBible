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
 * File: Migration_2_3.java
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.migration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.logger.StaticLogger;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class Migration_2_3 extends Migration {

    private static final Object TAG = Migration_2_3.class.getSimpleName();

    public Migration_2_3() {
        super(2, 3);
    }

    @Override
    public void migrate(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE bookmarks ADD COLUMN time INTEGER NOT NULL DEFAULT 0;");
        Cursor cursor = db.rawQuery("SELECT * FROM bookmarks", null);
        if (cursor.moveToFirst()) {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            String date;
            do {
                date = cursor.getString(cursor.getColumnIndex(Bookmark.DATE));
                ContentValues cv = new ContentValues(1);
                try {
                    cv.put(Bookmark.TIME, dateFormat.parse(date).getTime());
                } catch (ParseException ex) {
                    StaticLogger.error(TAG, "Failure update time", ex);
                    cv.put(Bookmark.TIME, new Date().getTime());
                }
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                db.update("bookmarks", cv, "_id=?", new String[]{id});
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Добавляем триггеры

        db.execSQL(// удаляем ссылки на теги при удалении закладки
                "CREATE TRIGGER delete_bookmark AFTER DELETE ON bookmarks FOR EACH ROW " +
                        "BEGIN " +
                        "DELETE FROM bookmarks_tags WHERE OLD._id=bookmarks_tags.bm_id; " +
                        "END");
        db.execSQL( // при удалении ссылок на теги удаляем непривязанные теги
                "CREATE TRIGGER delete_bookmark_tags AFTER DELETE ON bookmarks_tags " +
                        "BEGIN " +
                        "DELETE FROM tags WHERE tags._id NOT IN (SELECT DISTINCT bookmarks_tags.tag_id FROM bookmarks_tags); " +
                        "END");
        db.execSQL( // при обновлении закладки удаляем все ссылки на теги (теги надо добавить заново)
                "CREATE TRIGGER update_bookmark AFTER UPDATE ON bookmarks FOR EACH ROW " +
                        "BEGIN " +
                        "DELETE FROM bookmarks_tags WHERE OLD._id=bookmarks_tags.bm_id; " +
                        "END");
        db.execSQL( // при удалении тега удаляем ссылки на него
                "CREATE TRIGGER delete_tag AFTER DELETE ON tags FOR EACH ROW " +
                        "BEGIN " +
                        "DELETE FROM bookmarks_tags WHERE OLD._id=bookmarks_tags.tag_id; " +
                        "END");

        // Добавляем представления

        db.execSQL(
                "CREATE VIEW bm_tags AS " +
                        "SELECT bookmarks_tags.bm_id AS bm_id, tags._id AS tag_id, tags.name AS tag_name " +
                        "FROM bookmarks_tags " +
                        "JOIN tags ON bookmarks_tags.tag_id=tags._id " +
                        "ORDER BY bookmarks_tags.bm_id;");
    }
}
