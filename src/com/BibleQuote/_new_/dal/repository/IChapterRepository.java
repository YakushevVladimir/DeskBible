package com.BibleQuote._new_.dal.repository;

import java.util.Collection;

import com.BibleQuote._new_.models.Chapter;

public interface IChapterRepository<TBook> {
    
	/*
	 * Data source related methods
	 * 
	 */
	Collection<Chapter> loadChapters(TBook book);
	
	Chapter loadChapter(TBook book, Integer chapterNumber);
	
	void insertChapter(Chapter chapter);
    
	void deleteChapter(Chapter chapter);
	
	void updateChapter(Chapter chapter);
	
	/*
	 * Internal cache related methods
	 *
	 */
	Collection<Chapter> getChapters(TBook book);
	
	Chapter getChapterByNumber(TBook book, Integer chapterNumber);

}
