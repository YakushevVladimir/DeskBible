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

import com.BibleQuote.dal.DbLibraryUnitOfWork;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Verse;

public class DbChapterController implements IChapterController {
	//private final String TAG = "DbChapterController";
	
	//private IBookRepository<DbModule, DbBook> br;

	public DbChapterController(DbLibraryUnitOfWork unit) {
		//br = unit.getBookRepository();
    }
	
	public Chapter getChapter(Book book, Integer chapterNumber) {
		ArrayList<String> verseNumbers = new ArrayList<String>();
		verseNumbers.add("1");
		verseNumbers.add("2");
		verseNumbers.add("3");
		
		ArrayList<Verse> verseList = new ArrayList<Verse>();
		verseList.add(new Verse(1, "This is a verse number 1"));
		verseList.add(new Verse(2, "This is a verse number 2"));
		verseList.add(new Verse(3, "This is a verse number 3"));
		
		//Chapter chapter = new Chapter(chapterNumber, verseNumbers, verseList);
		//chapter.Text = " This is a chapter #" + chapterNumber; 
		return null;
	}
	
	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) {
		Chapter chapter = getChapter(book, chapterNumber);
		return chapter.getVerseNumbers();
	}


	public ArrayList<Chapter> getChapterList(Book book) {
		return null;
	}

	public String getChapterHTMLView(Chapter chapter) {
		return null;
	}


}
