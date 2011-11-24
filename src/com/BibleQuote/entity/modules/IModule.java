package com.BibleQuote.entity.modules;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote.entity.Book;

public interface IModule {

	public abstract ArrayList<Book> getBooks();

	public abstract String getShortName();

	public abstract String getName();

	public abstract boolean isBible();

	/**
	 * @return Return 'true', if module contains Storng numbers, else 'false'
	 */
	public abstract boolean isContainsStrong();

	public abstract String getHtmlFilter();

	public abstract Boolean containsChapterZero();

	public abstract Book getBook(String bookID);

	/**
	 * Return list of book chapters
	 */
	public abstract ArrayList<String> getChapters(String bookID);

	public abstract ArrayList<String> getChapterVerses(Book book,
			Integer chapterToView);

	public abstract LinkedHashMap<String, String> search(String query,
			String fromBookID, String toBookID);

}