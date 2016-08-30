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
 * File: SimpleBibleShareFormatter.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.textFormatters;

import java.util.LinkedHashMap;

public class SimpleBibleShareFormatter implements IShareTextFormatter {
	private LinkedHashMap<Integer, String> verses;

	public SimpleBibleShareFormatter(LinkedHashMap<Integer, String> verses) {
		this.verses = verses;
	}

	@Override
	public String format() {
		StringBuilder shareText = new StringBuilder();

		Integer prevVerseNumber = 0;
		for (Integer verseNumber : verses.keySet()) {
			if (prevVerseNumber == 0) {
				prevVerseNumber = verseNumber;
			}

			if (verseNumber - prevVerseNumber > 1) {
				shareText.append(" ... ");
			} else if (shareText.length() != 0) {
				shareText.append(" ");
			}
			shareText.append(verses.get(verseNumber).trim());
			prevVerseNumber = verseNumber;
		}

		return shareText.toString();
	}

}
