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
 * File: ShareBuilder.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.share;

import android.content.Context;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;

import java.util.LinkedHashMap;

public class ShareBuilder {

	public enum Destination {
		Clipboard, ActionSend
	}

	private Context context;
    private BaseModule module;
    private Book book;
    private Chapter chapter;
    private LinkedHashMap<Integer, String> verses;

    public ShareBuilder(Context context, BaseModule module, Book book, Chapter chapter, LinkedHashMap<Integer, String> verses) {
        this.context = context;
        this.module = module;
        this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	public void share(Destination dest) {
		BaseShareBuilder builder = getBuilder(dest);
		if (builder == null) {
			return;
		}
		builder.share();
	}

	private BaseShareBuilder getBuilder(Destination dest) {
		if (dest == Destination.ActionSend) {
			return new ActionSendShare(context, module, book, chapter, verses);
		} else if (dest == Destination.Clipboard) {
			return new ClipboardShare(context, module, book, chapter, verses);
		} else {
			return null;
		}
	}

}
