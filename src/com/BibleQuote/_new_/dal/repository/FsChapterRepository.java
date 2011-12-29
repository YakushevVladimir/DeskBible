package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.FsBook;

public class FsChapterRepository implements IChapterRepository<FsBook> {

	private FsLibraryContext context;
	
    public FsChapterRepository(FsLibraryContext context) {
    	this.context = context;
    }
    	
	
	@Override
	public Collection<Chapter> loadChapters(FsBook book) {
		if (!context.isBookLoaded(book)) {
			context.bookSet.put(book.Name, book);
		}
		
		context.chapterSet = new LinkedHashMap<Integer, Chapter>();
		
		BufferedReader reader = context.getBookReader(book);
		ArrayList<String> numbers = book.getChapterNumbers(book.getModule().ChapterZero);
		for (String chapterNumber : numbers) {
			Chapter chapter = context.loadChapter(book, Integer.valueOf(chapterNumber), reader);
			context.chapterSet.put(Integer.valueOf(chapterNumber), chapter);
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		return context.getChapterList(context.chapterSet); 	
	}

	
	@Override
	public Chapter loadChapter(FsBook book, Integer chapterNumber) {
		if (!context.isBookLoaded(book)) {
			context.bookSet.put(book.Name, book);
		}		
		
		BufferedReader reader = context.getBookReader(book);
		Chapter chapter = context.loadChapter(book, chapterNumber, reader);
		context.chapterSet.put(chapterNumber, chapter);
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		return chapter;
	}

	
	@Override
	public void insertChapter(Chapter chapter) {
	}
	

	@Override
	public void deleteChapter(Chapter chapter) {
	}
	

	@Override
	public void updateChapter(Chapter chapter) {
	}

	
	@Override
	public Collection<Chapter> getChapters(FsBook book) {
		return context.getChapterList(context.chapterSet);	
	}
	

	@Override
	public Chapter getChapterByNumber(FsBook book, Integer chapterNumber) {
		if (!context.isBookLoaded(book)) {
			context.bookSet.put(book.Name, book);
		}
		
		return context.chapterSet.get(chapterNumber);
	}


	
	
}
