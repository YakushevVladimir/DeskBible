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
 * File: BaseShareBuilder.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.utils.share;

import android.content.Context;

import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.bibleReferenceFormatter.EmptyReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.FullReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.IBibleReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.ShortReferenceFormatter;
import com.BibleQuote.utils.textFormatters.BreakVerseBibleShareFormatter;
import com.BibleQuote.utils.textFormatters.IShareTextFormatter;
import com.BibleQuote.utils.textFormatters.SimpleBibleShareFormatter;

import java.util.LinkedHashMap;
import java.util.TreeSet;

public abstract class BaseShareBuilder {
	IShareTextFormatter textFormater;
	IBibleReferenceFormatter referenceFormatter;

	Context context;
	Module module;
	Book book;
	Chapter chapter;
	LinkedHashMap<Integer, String> verses;

	protected void initFormatters() {
		if (PreferenceHelper.divideTheVerses()) {
			textFormater = new BreakVerseBibleShareFormatter(verses);
		} else {
			textFormater = new SimpleBibleShareFormatter(verses);
		}

		TreeSet<Integer> verseNumbers = new TreeSet<Integer>();
		for (Integer numb : verses.keySet()) {
			verseNumbers.add(numb);
		}

		String chapterNumber = String.valueOf(chapter.getNumber());
		if (!PreferenceHelper.addReference()) {
			referenceFormatter = new EmptyReferenceFormatter(module, book, chapterNumber, verseNumbers);
		} else if (PreferenceHelper.shortReference()) {
			referenceFormatter = new ShortReferenceFormatter(module, book, chapterNumber, verseNumbers);
		} else {
			referenceFormatter = new FullReferenceFormatter(module, book, chapterNumber, verseNumbers);
		}
	}

	protected String getShareText() {
		String text = textFormater.format();
		if (!PreferenceHelper.addReference()) {
			return text;
		}

		String reference = referenceFormatter.getLink();
		if (PreferenceHelper.putReferenceInBeginning()) {
			return String.format("%1$s - %2$s", reference, text);
		} else {
			return String.format("%1$s (%2$s)", text, reference);
		}
	}

	public abstract void share();
}
