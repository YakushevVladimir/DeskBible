package com.BibleQuote.listeners;

import com.BibleQuote.modules.Book;

import java.util.ArrayList;

public class SearchInLibraryEvent {

	public ISearchListener.SearchCode code;
	public ArrayList<Book> books;
}
