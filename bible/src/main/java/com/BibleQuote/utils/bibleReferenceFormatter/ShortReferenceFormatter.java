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
 * File: ShortReferenceFormatter.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.bibleReferenceFormatter;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.utils.PreferenceHelper;

import java.util.TreeSet;

public class ShortReferenceFormatter extends ReferenceFormatter implements IBibleReferenceFormatter {

    public ShortReferenceFormatter(BaseModule module, Book book, String chapter,
            TreeSet<Integer> verses) {
        super(module, book, chapter, verses);
    }

	@Override
	public String getLink() {
		PreferenceHelper prefHelper = BibleQuoteApp.getInstance().getPrefHelper();
		String result = String.format(
				"%1$s.%2$s:%3$s",
				book.getShortName(), chapter, getVerseLink());
		if (prefHelper.addModuleToBibleReference()) {
			result = String.format("%1$s | %2$s", result, module.getID());
		}
		return result;
	}

}
