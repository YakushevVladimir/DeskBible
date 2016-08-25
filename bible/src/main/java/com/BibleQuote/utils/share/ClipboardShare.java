/*
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
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: ClipboardShare.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.utils.share;

import android.content.Context;
import android.text.ClipboardManager;

import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Module;

import java.util.LinkedHashMap;

public class ClipboardShare extends BaseShareBuilder {

	public ClipboardShare(Context context, Module module, Book book,
						  Chapter chapter, LinkedHashMap<Integer, String> verses) {
		this.context = context;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void share() {
		initFormatters();
		if (textFormater == null || referenceFormatter == null) {
			return;
		}

		ClipboardManager clpbdManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (clpbdManager != null) {
			clpbdManager.setText(getShareText());
		}
	}

}
