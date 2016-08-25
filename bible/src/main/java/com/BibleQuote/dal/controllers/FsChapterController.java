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
 * File: FsChapterController.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.controllers;

import com.BibleQuote.dal.FsLibraryUnitOfWork;
import com.BibleQuote.domain.controllers.IChapterController;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.entity.Verse;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.repository.IBookRepository;
import com.BibleQuote.domain.repository.IChapterRepository;
import com.BibleQuote.entity.modules.BQBook;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.textFormatters.ITextFormatter;
import com.BibleQuote.utils.textFormatters.ModuleTextFormatter;

import java.util.ArrayList;

public class FsChapterController implements IChapterController {

	private IBookRepository<BQModule, BQBook> bRepository;
	private IChapterRepository<BQBook> chRepository;


	public FsChapterController(FsLibraryUnitOfWork unit) {
		bRepository = unit.getBookRepository();
		chRepository = unit.getChapterRepository();
	}


	public ArrayList<Chapter> getChapterList(Book book) throws BookNotFoundException {
		book = getValidBook(book);
		ArrayList<Chapter> chapterList = (ArrayList<Chapter>) chRepository.getChapters((BQBook) book);
		if (chapterList.size() == 0) {
			chapterList = (ArrayList<Chapter>) chRepository.loadChapters((BQBook) book);
		}
		return chapterList;
	}


	public Chapter getChapter(Book book, Integer chapterNumber) throws BookNotFoundException {
		book = getValidBook(book);
		Chapter chapter = chRepository.getChapterByNumber((BQBook) book, chapterNumber);
		if (chapter == null) {
			chapter = chRepository.loadChapter((BQBook) book, chapterNumber);
		}
		return chapter;
	}


	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) throws BookNotFoundException {
		book = getValidBook(book);
		Chapter chapter = chRepository.getChapterByNumber((BQBook) book, chapterNumber);
		if (chapter == null) {
			chapter = chRepository.loadChapter((BQBook) book, chapterNumber);
		}
		return chapter.getVerseNumbers();
	}


	public String getChapterHTMLView(Chapter chapter) {
		if (chapter == null) {
			return "";
		}

		ITextFormatter formatter = new ModuleTextFormatter(chapter.getBook().getModule());
		ArrayList<Verse> verses = chapter.getVerseList();
		StringBuilder chapterHTML = new StringBuilder();
		for (int verse = 1; verse <= verses.size(); verse++) {
			String verseText = formatter.format(verses.get(verse - 1).getText());
			chapterHTML.append("<div id=\"verse_").append(verse).append("\" class=\"verse\">")
					.append(verseText.replaceAll("<(/)*div(.*?)>", "<$1p$2>"))
					.append("</div>")
					.append("\r\n");
		}

		return chapterHTML.toString();
	}


	private Book getValidBook(Book book) throws BookNotFoundException {
		String moduleID = book.getModule().getID();
		String bookID = book.getID();
		try {
			Module module = book.getModule();
			book = bRepository.getBookByID((BQModule) module, book.getID());
			if (book == null) {
				throw new BookNotFoundException(module.getID(), bookID);
			}
		} catch (Exception e) {
			throw new BookNotFoundException(moduleID, bookID);
		}
		return book;
	}
}
