package com.BibleQuote.dal.repository;

import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.modules.Chapter;

import java.util.Collection;

public interface IChapterRepository<T> {

	/*
	 * Data source related methods
	 * 
	 */
	Collection<Chapter> loadChapters(T book) throws BookNotFoundException;

	Chapter loadChapter(T book, Integer chapterNumber) throws BookNotFoundException;

	void insertChapter(Chapter chapter);

	void deleteChapter(Chapter chapter);

	void updateChapter(Chapter chapter);

	/*
	 * Internal cache related methods
	 *
	 */
	Collection<Chapter> getChapters(T book);

	Chapter getChapterByNumber(T book, Integer chapterNumber);

}
