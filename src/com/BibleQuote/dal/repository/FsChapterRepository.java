package com.BibleQuote.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.FsBook;
import com.BibleQuote.utils.OSISLink;

public class FsChapterRepository implements IChapterRepository<FsBook> {

	private FsLibraryContext context;
	
    public FsChapterRepository(FsLibraryContext context) {
    	this.context = context;
    }
    	
	
	public Collection<Chapter> loadChapters(FsBook book) {
		BufferedReader reader = context.getBookReader(book);
		if (reader == null) {
			return new ArrayList<Chapter>();
		}
		ArrayList<String> numbers = book.getChapterNumbers(book.getModule().ChapterZero);
		for (String chapterNumber : numbers) {
			Chapter chapter = context.loadChapter(book, Integer.valueOf(chapterNumber), reader);
			OSISLink osislink = new OSISLink(book.getModule(), book, chapter.getNumber(), book.getFirstChapterNumber());
			context.chapterPool.put(osislink.getPath(), chapter);
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		return context.getChapterList(context.chapterPool); 	
	}

	
	public Chapter loadChapter(FsBook book, Integer chapterNumber) {
		BufferedReader reader = context.getBookReader(book);
		if (reader == null) {
			return null;
		}
		Chapter chapter = context.loadChapter(book, chapterNumber, reader);
		OSISLink osislink = new OSISLink(book.getModule(), book, chapterNumber, book.getFirstChapterNumber());
		context.chapterPool.put(osislink.getPath(), chapter);
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(); 
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
		OSISLink osislink = new OSISLink(book.getModule(), book, chapterNumber, book.getFirstChapterNumber());
		return context.chapterPool.get(osislink.getPath());
	}
	
}
