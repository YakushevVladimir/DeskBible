package com.BibleQuote.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;

public interface IBookController {
	
	public LinkedHashMap<String, Book> getBooks(Module module) throws ModuleNotFoundException, CreateModuleErrorException;
	
	public ArrayList<Book> getBookList(Module module) throws ModuleNotFoundException;
	
	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, ModuleNotFoundException;
	
	public LinkedHashMap<String, String> search(Module module, String query, String fromBookID, String toBookID) throws ModuleNotFoundException, CreateModuleErrorException, BookNotFoundException;
	
}
