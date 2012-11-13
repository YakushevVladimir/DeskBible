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

package com.BibleQuote.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import android.util.Log;

import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.FsBook;

public class FsChapterRepository implements IChapterRepository<FsBook> {
	private final String TAG = "FsChapterRepository";
	private FsLibraryContext context;
	
    public FsChapterRepository(FsLibraryContext context) {
    	this.context = context;
    }
    	
	
	public Collection<Chapter> loadChapters(FsBook book) throws BookNotFoundException {
		BufferedReader reader = null;
		String bookID = "";
		String moduleID = "";
		try {
			bookID = book.getID();
			moduleID = book.getModule().getID();
			reader = context.getBookReader(book);
			
			ArrayList<String> numbers = book.getChapterNumbers(book.getModule().ChapterZero);
			for (String chapterNumber : numbers) {
				Chapter chapter = context.loadChapter(book, Integer.valueOf(chapterNumber), reader);
				BibleReference osislink = new BibleReference(book.getModule(), book, chapter.getNumber(), book.getFirstChapterNumber());
				context.chapterPool.put(osislink.getPath(), chapter);
			}			
		} catch (FileAccessException e) {
			Log.e(TAG, "Can't load chapters of book with ID=" + bookID, e);
			throw new BookNotFoundException(moduleID, bookID);
						
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
		
		return context.getChapterList(context.chapterPool); 	
	}

	
	public Chapter loadChapter(FsBook book, Integer chapterNumber) throws BookNotFoundException {
		Chapter chapter = null;
		BufferedReader reader = null;
		String bookID = "";
		String moduleID = "";
		try {
			bookID = book.getID();
			moduleID = book.getModule().getID();		
			reader = context.getBookReader(book);
			
			chapter = context.loadChapter(book, chapterNumber, reader);
			BibleReference osislink = new BibleReference(book.getModule(), book, chapterNumber, book.getFirstChapterNumber());
			context.chapterPool.put(osislink.getPath(), chapter);
		} catch (FileAccessException e) {
			Log.e(TAG, "Can't load chapters of book with ID=" + bookID, e);
			throw new BookNotFoundException(moduleID, bookID);
						
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
		return chapter;
	}

	
	public void insertChapter(Chapter chapter) {
	}
	

	public void deleteChapter(Chapter chapter) {
	}
	

	public void updateChapter(Chapter chapter) {
	}

	
	public Collection<Chapter> getChapters(FsBook book) {
		return context.getChapterList(context.chapterPool);	
	}
	

	public Chapter getChapterByNumber(FsBook book, Integer chapterNumber) {
		BibleReference osislink = new BibleReference(book.getModule(), book, chapterNumber, book.getFirstChapterNumber());
		return context.chapterPool.get(osislink.getPath());
	}
	
}
