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

package com.BibleQuote.controllers;

import java.util.ArrayList;

import com.BibleQuote.dal.FsLibraryUnitOfWork;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.dal.repository.IChapterRepository;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.FsBook;
import com.BibleQuote.models.FsModule;
import com.BibleQuote.models.Module;
import com.BibleQuote.models.Verse;
import com.BibleQuote.utils.StringProc;

public class FsChapterController implements IChapterController {
	//private final String TAG = "FsChapterController";
	
	private IBookRepository<FsModule, FsBook> bRepository;
	private IChapterRepository<FsBook> chRepository;
	
	
	public FsChapterController(FsLibraryUnitOfWork unit) {
		bRepository = unit.getBookRepository();
		chRepository = unit.getChapterRepository();
    }
	

	public ArrayList<Chapter> getChapterList(Book book) throws BookNotFoundException {
		book = getValidBook(book);
		ArrayList<Chapter> chapterList = (ArrayList<Chapter>) chRepository.getChapters((FsBook)book);
		if (chapterList.size() == 0) {
			chapterList =  (ArrayList<Chapter>) chRepository.loadChapters((FsBook)book);
		}
		return chapterList;
	}

	
	public Chapter getChapter(Book book, Integer chapterNumber) throws BookNotFoundException {
		book = getValidBook(book);
		Chapter chapter = chRepository.getChapterByNumber((FsBook)book, chapterNumber);
		if (chapter == null) {
			chapter = chRepository.loadChapter((FsBook)book, chapterNumber);
		}
		return chapter;
	}
	
	
	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) throws BookNotFoundException {
		book = getValidBook(book);
		Chapter chapter = chRepository.getChapterByNumber((FsBook)book, chapterNumber);
		if (chapter == null) {
			chapter = chRepository.loadChapter((FsBook)book, chapterNumber);
		}
		return chapter.getVerseNumbers();
	}


	public String getChapterHTMLView(Chapter chapter) {
		if (chapter == null) {
			return "";
		}		
		Module currModule = chapter.getBook().getModule();
		
		ArrayList<Verse> verses = chapter.getVerseList(); 
		StringBuilder chapterHTML = new StringBuilder();
		for (int verse = 1; verse <= verses.size(); verse++) {
			String verseText = verses.get(verse - 1).getText();

			if (currModule.containsStrong) {
				// убираем номера Стронга
				verseText = verseText.replaceAll("\\s(\\d)+", "");
			}
			
			verseText = StringProc.stripTags(verseText, currModule.HtmlFilter, false);
			verseText = verseText.replaceAll("<a\\s+?href=\"verse\\s\\d+?\">(\\d+?)</a>", "<b>$1</b>");
			if (currModule.isBible) {
				verseText = verseText
						.replaceAll("^(<[^/]+?>)*?(\\d+)(</(.)+?>){0,1}?\\s+",
								"$1<b>$2</b>$3 ").replaceAll(
								"null", "");
			}

			chapterHTML.append(
				"<div id=\"verse_" + verse + "\" class=\"verse\">"
				+ verseText.replaceAll("<(/)*div(.*?)>", "<$1p$2>")
				+ "</div>"
				+ "\r\n");
		}

		return chapterHTML.toString();
	}

	
	private Book getValidBook(Book book) throws BookNotFoundException {
		String moduleID = null;
		String bookID = null;
		try {
			Module module = book.getModule();
			book = bRepository.getBookByID((FsModule)module, book.getID());
			if (book == null) {
				throw new BookNotFoundException(moduleID, bookID);
			}
		} catch (Exception e) {
			throw new BookNotFoundException(moduleID, bookID);
		}
		return book;
	}
}
