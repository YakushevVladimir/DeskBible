package com.BibleQuote.listeners;

import java.util.ArrayList;

import com.BibleQuote.models.Book;

public class SearchInLibraryEvent {
	
	public ISearchListener.SearchCode code;
	public ArrayList<Book> books;
}
