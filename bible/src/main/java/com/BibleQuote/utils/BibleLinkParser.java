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
 * File: BibleLinkParser.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.utils;

import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.managers.BibleBooksID;

import java.util.LinkedHashSet;

public final class BibleLinkParser {

	private static final String TO_VERSE_SEPARATOR = "-";
	private static final String LINK_SEPARATOR = ";";

	private BibleLinkParser() throws InstantiationException {
		throw new InstantiationException("This class is not for instantiation");
	}

	public static LinkedHashSet<BibleReference> parse(String moduleID, String references) {

		LinkedHashSet<BibleReference> bibleLinks = new LinkedHashSet<BibleReference>();
		String currSymbol;
		StringBuilder book, chapter, fromVerse, toVerse;

		references = references.toLowerCase().replaceAll("\\s+?", "").replaceAll("\\.", "");

		for (String currLink : references.split(LINK_SEPARATOR)) {
			currSymbol = "";
			book = new StringBuilder();
			chapter = new StringBuilder();
			fromVerse = new StringBuilder();
			toVerse = new StringBuilder();
			int currPos = 0;

			// Parse book
			while (currPos < (currLink.length())) {
				currSymbol = currLink.substring(currPos, currPos + 1);
				if (isDigit(currSymbol) && currPos != 0) {
					break;
				}
				currPos++;
				book.append(currSymbol);
			}
			if (book.length() == 0) {
				continue;
			} else {
				book = new StringBuilder(BibleBooksID.getID(book.toString()));
			}

			// Parse chapter
			while (currPos < (currLink.length())) {
				currSymbol = currLink.substring(currPos, currPos + 1);
				currPos++;
				if (!isDigit(currSymbol)) {
					break;
				}
				chapter.append(currSymbol);
			}
			if (chapter.length() == 0) {
				continue;
			}

			// Parse fromVerse
			while (currPos < (currLink.length())) {
				currSymbol = currLink.substring(currPos, currPos + 1);
				currPos++;
				if (!isDigit(currSymbol)) {
					break;
				}
				fromVerse.append(currSymbol);
			}
			if (fromVerse.length() == 0) {
				continue;
			}

			// Parse toVerse
			if (!currSymbol.equals(TO_VERSE_SEPARATOR)) {
				toVerse = fromVerse;
			} else {
				while (currPos < (currLink.length())) {
					currSymbol = currLink.substring(currPos, currPos + 1);
					currPos++;
					if (!isDigit(currSymbol)) {
						break;
					}
					toVerse.append(currSymbol);
				}
				if (toVerse.length() == 0) {
					toVerse = fromVerse;
				}
			}

			bibleLinks.add(new BibleReference(
					moduleID,
					book.toString(),
					Integer.parseInt(chapter.toString()),
					Integer.parseInt(fromVerse.toString()),
					Integer.parseInt(toVerse.toString())));
		}

		return bibleLinks;
	}

	public static boolean isDigit(String symbol) {
		return "0123456789".contains(symbol);
	}

}
