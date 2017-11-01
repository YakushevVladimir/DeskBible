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
 * File: Bookmark.java
 *
 * Created by Vladimir Yakushev at 11/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.entity;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Vladimir Yakushev
 * Date: 09.04.13
 * Time: 1:00
 */
public class Bookmark {

    public static final String KEY_ID = "_id";
    public static final String OSIS = "osis";
    public static final String LINK = "link";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String TIME = "time";

    public String OSISLink;
    public String date;
    public String humanLink;
    public Long id;
    public String name;
    public String tags;
    public long time;

    public Bookmark(BibleReference ref) {
        this(ref.getPath(), ref.toString());
    }

    public Bookmark(String OSISLink, String humanLink) {
        this(null, "", OSISLink, humanLink, getBookmarkDate());
    }

    public Bookmark(Long id, String name, String osisLink, String humanLink, String date) {
        this(id, name, osisLink, humanLink, date, null);
    }

    public Bookmark(Long id, String name, String OSISLink, String humanLink, String date, String tags) {
        this.id = id;
        this.name = name;
        this.OSISLink = OSISLink;
        this.humanLink = humanLink;
        this.date = date;
        this.tags = tags;
        this.time = new Date().getTime();
    }

    public Bookmark(long id, String name, String OSISLink, String humanLink, String date, long time) {
        this.id = id;
        this.name = name;
        this.OSISLink = OSISLink;
        this.humanLink = humanLink;
        this.date = date;
        this.time = time;
    }

    @NonNull
    public static Bookmark fromCursor(Cursor cursor) {
        return new Bookmark(
                cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(NAME)),
                cursor.getString(cursor.getColumnIndex(OSIS)),
                cursor.getString(cursor.getColumnIndex(LINK)),
                cursor.getString(cursor.getColumnIndex(DATE)),
                cursor.getLong(cursor.getColumnIndex(TIME)));
    }

    private static String getBookmarkDate() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Bookmark) {
            return this.id.equals(((Bookmark) obj).id);
        }

        return false;
    }

    @Override
    public String toString() {
        return humanLink;
    }
}
