package com.BibleQuote._new_.listeners;

import java.util.ArrayList;

import com.BibleQuote._new_.models.Book;

public class SearchInLibraryEvent {
	
	public ISearchListener.SearchCode code;
	public ArrayList<Book> books;
}
