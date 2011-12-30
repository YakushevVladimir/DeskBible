package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.ModuleNotFoundException;

public interface IBookController {
	
	public LinkedHashMap<String, Book> getBooks(Module module) throws ModuleNotFoundException;
	
	public ArrayList<Book> getBookList(Module module) throws ModuleNotFoundException;
	
	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, ModuleNotFoundException;
	
	public LinkedHashMap<String, String> search(Module module, String query, String fromBookID, String toBookID) throws ModuleNotFoundException;
	
}
