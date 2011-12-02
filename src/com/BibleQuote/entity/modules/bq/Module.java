package com.BibleQuote.entity.modules.bq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote.entity.Book;
import com.BibleQuote.entity.modules.IModule;

public abstract class Module implements IModule, Serializable {

	private static final long serialVersionUID = 443137641904273487L;
	protected String Name = "";
	protected String ShortName = "";
	protected String ChapterSign = "";
	protected String VerseSign = "";
	protected String XFilter = "";
	protected boolean ChapterZero = false;
	protected boolean containsStrong = false;
	protected boolean isBible = false;
	protected String defaultEncoding = "utf-8";
	protected LinkedHashMap<String, Book> Books = new LinkedHashMap<String, Book>();
	protected LinkedHashMap<String, String> SearchRes = new LinkedHashMap<String, String>();
	
	// private String Categories = "";
	// private String Copyright = "";
	// private boolean containsOT = false;
	// private boolean containsNT = false;
	// private boolean containsAP = false;
	
	@Override
	public ArrayList<Book> getBooks() {
		ArrayList<Book> books = new ArrayList<Book>();
		for (Book currBook : Books.values()) {
			books.add(currBook);
		}
		return books;
	}

	@Override
	public String getShortName() {
		return ShortName;
	}

	@Override
	public String getName() {
		return Name;
	}

	@Override
	public boolean isBible() {
		return this.isBible;
	}

	@Override
	public boolean isContainsStrong() {
		return containsStrong;
	}

	@Override
	public String getHtmlFilter() {
		return XFilter;
	}

	@Override
	public Boolean containsChapterZero() {
		return this.ChapterZero;
	}

	@Override
	public Book getBook(String bookID) {
		if (bookID == null || !Books.containsKey(bookID)) {
			return null;
		}
		return Books.get(bookID);
	}

	@Override
	public ArrayList<String> getChapters(String bookID) {
		ArrayList<String> ret = new ArrayList<String>();
		Book currBook = this.getBook(bookID);
		if (currBook != null) {
			for (int i = 0; i < currBook.getChapterQty(); i++) {
				ret.add(String.valueOf(i + (this.ChapterZero ? 0 : 1)));
			}
		}
		return ret;
	}

	@Override
	public abstract ArrayList<String> getChapterVerses(Book book, Integer chapterToView);

	@Override
	public LinkedHashMap<String, String> search(String query, String fromBookID, String toBookID) {
		LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
	
		if (query.trim().equals("")) {
			// Передана пустая строка
			return ret;
		}
	
		// Подготовим регулярное выражение для поиска
		String regQuery = "";
		String[] words = query.toLowerCase().split("\\s+");
		for (String currWord : words) {
			regQuery += (regQuery.equals("") ? "" : "\\s(.)*?") + currWord;
		}
		regQuery = ".*?" + regQuery + ".*?"; // любые символы в начале и конце
	
		SearchRes.clear();
		
		boolean startSearch = false;
		for (String bookID : Books.keySet()) {
			if (!startSearch) {
				startSearch = bookID.equals(fromBookID);
				if (!startSearch) {
					continue;
				}
			} 
			searchInBook(bookID, regQuery);
			if (bookID.equals(toBookID)) {
				break;
			}
		}
		return SearchRes;
	}
	
	protected abstract void searchInBook(String bookID, String regQuery);

	public String toString() {
		return this.Name;
	}
}