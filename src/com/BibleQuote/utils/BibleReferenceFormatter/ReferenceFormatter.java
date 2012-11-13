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

package com.BibleQuote.utils.BibleReferenceFormatter;

import java.util.TreeSet;

import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;

public abstract class ReferenceFormatter implements IBibleReferenceFormatter  {
	protected Module module;
	protected Book book;
	protected String chapter;
	protected TreeSet<Integer> verses;
	
	public ReferenceFormatter(Module module, Book book, String chapter,
			TreeSet<Integer> verses) {
		super();
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}
	
	protected String getOnLineBibleLink() {
		return "http://b-bq.eu/" 
				+ book.OSIS_ID + "/" + chapter + "_" + getVerseLink() 
				+ "/" + module.ShortName; 

	}

	protected String getVerseLink() {
		StringBuilder verseLink = new StringBuilder();
		Integer fromVerse = 0;
		Integer toVerse = 0;
		for (Integer verse : verses) {
			if (fromVerse == 0) {
				fromVerse = verse;
			} else if ((toVerse + 1) != verse) {
				if (verseLink.length() != 0) {
					verseLink.append(",");
				}
				if (fromVerse == toVerse) {
					verseLink.append(fromVerse);
				} else {
					verseLink.append(fromVerse + "-" + toVerse);
				}
				fromVerse = verse;
			}
			toVerse = verse;
		}
		if (verseLink.length() != 0) {
			verseLink.append(",");
		}
		if (fromVerse == toVerse) {
			verseLink.append(fromVerse);
		} else {
			verseLink.append(fromVerse + "-" + toVerse);
		}
		
		return verseLink.toString();
	}
}
