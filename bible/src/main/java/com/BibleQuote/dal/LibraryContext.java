/*
 * Copyright (c) 2011-2015 Scripture Software
 * http://www.scripturesoftware.org
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.BibleQuote.dal;

import android.content.Context;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Chapter;
import com.BibleQuote.modules.Module;
import com.BibleQuote.utils.CachePool;

import java.io.File;
import java.util.*;

public abstract class LibraryContext {

	public Map<String, Module> moduleSet = Collections.synchronizedMap(new TreeMap<String, Module>());
	public Map<String, Book> bookSet = Collections.synchronizedMap(new LinkedHashMap<String, Book>());
	public Map<String, Chapter> chapterPool = Collections.synchronizedMap(new CachePool<Chapter>());

	private Context context;

	public Context getContext() {
		return context;
	}

	public LibraryContext(Context context) {
		this.context = context;
	}

	protected HashMap<String, String> getCharsets() {
		HashMap<String, String> charsets = new HashMap<String, String>();
		charsets.put("0", "ISO-8859-1"); // ANSI charset
		charsets.put("1", "US-ASCII"); // DEFAULT charset
		charsets.put("77", "MacRoman"); // Mac Roman
		charsets.put("78", "Shift_JIS"); // Mac Shift Jis
		charsets.put("79", "ms949"); // Mac Hangul
		charsets.put("80", "GB2312"); // Mac GB2312
		charsets.put("81", "Big5"); // Mac Big5
		charsets.put("82", "johab"); // Mac Johab (old)
		charsets.put("83", "MacHebrew"); // Mac Hebrew
		charsets.put("84", "MacArabic"); // Mac Arabic
		charsets.put("85", "MacGreek"); // Mac Greek
		charsets.put("86", "MacTurkish"); // Mac Turkish
		charsets.put("87", "MacThai"); // Mac Thai
		charsets.put("88", "cp1250"); // Mac East Europe
		charsets.put("89", "cp1251"); // Mac Russian
		charsets.put("128", "MS932"); // Shift JIS
		charsets.put("129", "ms949"); // Hangul
		charsets.put("130", "ms1361"); // Johab
		charsets.put("134", "ms936"); // GB2312
		charsets.put("136", "ms950"); // Big5
		charsets.put("161", "cp1253"); // Greek
		charsets.put("162", "cp1254"); // Turkish
		charsets.put("163", "cp1258"); // Vietnamese
		charsets.put("177", "cp1255"); // Hebrew
		charsets.put("178", "cp1256"); // Arabic
		charsets.put("186", "cp1257"); // Baltic
		charsets.put("201", "cp1252"); // Cyrillic charset
		charsets.put("204", "cp1251"); // Russian
		charsets.put("222", "ms874"); // Thai
		charsets.put("238", "cp1250"); // Eastern European
		charsets.put("254", "cp437"); // PC 437
		charsets.put("255", "cp850"); // OEM

		return charsets;
	}

	public abstract File getLibraryDir();
}
