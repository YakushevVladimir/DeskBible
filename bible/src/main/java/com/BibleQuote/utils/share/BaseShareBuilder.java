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
 * File: BaseShareBuilder.java
 *
 * Created by Vladimir Yakushev at 3/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.share;

import android.content.Context;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.textFormatters.BreakVerseBibleShareFormatter;
import com.BibleQuote.domain.textFormatters.IShareTextFormatter;
import com.BibleQuote.domain.textFormatters.SimpleBibleShareFormatter;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.bibleReferenceFormatter.EmptyReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.FullReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.IBibleReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.ShortReferenceFormatter;

import java.util.LinkedHashMap;
import java.util.TreeSet;

abstract class BaseShareBuilder {
	Book book;
	Chapter chapter;
	Context context;
	Module module;
	IBibleReferenceFormatter referenceFormatter;
	IShareTextFormatter textFormatter;
	LinkedHashMap<Integer, String> verses;
	private PreferenceHelper preferenceHelper = BibleQuoteApp.getInstance().getPrefHelper();

	public abstract void share();

	String getShareText() {
		String text = textFormatter.format();
		if (!preferenceHelper.addReference()) {
			return text;
		}

		String reference = referenceFormatter.getLink();
		if (preferenceHelper.putReferenceInBeginning()) {
			return String.format("%1$s - %2$s", reference, text);
		} else {
			return String.format("%1$s (%2$s)", text, reference);
		}
	}

	void initFormatters() {
		if (preferenceHelper.divideTheVerses()) {
			textFormatter = new BreakVerseBibleShareFormatter(verses);
        } else {
            textFormatter = new SimpleBibleShareFormatter(verses);
        }

		TreeSet<Integer> verseNumbers = new TreeSet<>();
		for (Integer numb : verses.keySet()) {
			verseNumbers.add(numb);
		}

		String chapterNumber = String.valueOf(chapter.getNumber());
        if (!preferenceHelper.addReference()) {
            referenceFormatter = new EmptyReferenceFormatter(module, book, chapterNumber, verseNumbers);
        } else if (preferenceHelper.shortReference()) {
            referenceFormatter = new ShortReferenceFormatter(module, book, chapterNumber, verseNumbers);
		} else {
			referenceFormatter = new FullReferenceFormatter(module, book, chapterNumber, verseNumbers);
		}
	}
}
