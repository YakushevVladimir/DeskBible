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
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.entity;

import com.BibleQuote.dal.repository.bookmarks.DbBookmarksTagsRepository;

import java.text.DateFormat;
import java.util.Calendar;

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

	public long id;
	public String OSISLink;
	public String humanLink;
	public String name;
	public String date;
	public String tags;

	public Bookmark(long id, String osisLink, String humanLink, String name, String date) {
		this.id = id;
		this.OSISLink = osisLink;
		this.humanLink = humanLink;
		this.name = (name == null || name.equals("")) ? humanLink : name ;
		this.date = date;
        this.tags = new DbBookmarksTagsRepository().getTags(id);
    }

	public Bookmark(String OSISLink, String humanLink) {
		this(0, OSISLink, humanLink, humanLink, DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime()));
	}

	public Bookmark(String osisLink, String humanLink, String date) {
		this(0, osisLink, humanLink, humanLink, date);
	}

	public Bookmark(BibleReference ref) {
		this(ref.getPath(), ref.toString());
	}

	@Override
	public String toString() {
		return humanLink;
	}
}
