/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.managers.bookmarks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * User: Vladimir Yakushev
 * Date: 09.04.13
 * Time: 1:00
 */
public class Bookmark {
	public int id;
	public String OSISLink;
	public String humanLink;
	public String date;

	public Bookmark(String OSISLink, String humanLink) {
		this(0, OSISLink, humanLink, DateFormat.getDateInstance(DateFormat.MEDIUM).format(Calendar.getInstance().getTime()));
	}

	public Bookmark(int id, String OSISLink, String humanLink, String date) {
		this.id = id;
		this.OSISLink = OSISLink;
		this.humanLink = humanLink;
		this.date = date;
	}

	@Override
	public String toString() {
		return humanLink;
	}
}
