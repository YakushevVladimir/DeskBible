package com.BibleQuote._new_.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public abstract class Module implements Serializable {
	
	private static final long serialVersionUID = -499369158022814559L;
	
	public String Name = "";
	public String ShortName = "";
	public String ChapterSign = "";
	public String VerseSign = "";
	public String XFilter = "";
	public boolean ChapterZero = false;
	public boolean containsStrong = false;
	public boolean isBible = false;
	public String defaultEncoding = "utf-8";
	
	public LinkedHashMap<String, Book> Books = new LinkedHashMap<String, Book>();
	
	/**
	 * Идентификатор модуля в БД
	 */
	public Long Id;
	
	// private String Categories = "";
	// private String Copyright = "";
	// private boolean containsOT = false;
	// private boolean containsNT = false;
	// private boolean containsAP = false;
	
	public ArrayList<Book> getBooks() {
		ArrayList<Book> books = new ArrayList<Book>();
		for (Book currBook : Books.values()) {
			books.add(currBook);
		}
		return books;
	}

	public Book getBook(String bookID) {
		if (bookID == null || !Books.containsKey(bookID)) {
			return null;
		}
		return Books.get(bookID);
	}

	public String toString() {
		return this.Name;
	}

}