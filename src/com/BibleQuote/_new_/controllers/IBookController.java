package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Module;

public interface IBookController {
	
	public LinkedHashMap<String, Book> loadBooks(Module module);
	
	public void loadBooksAsync(Module module);
	
	public LinkedHashMap<String, Book> getBooks(Module module);
	
	public ArrayList<Book> getBookList(Module module);
	
	public Book getBook(Module module, String bookID);
}
