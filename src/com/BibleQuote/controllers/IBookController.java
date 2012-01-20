package com.BibleQuote.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;

public interface IBookController {
	
	public LinkedHashMap<String, Book> getBooks(Module module) throws OpenModuleException, CreateModuleErrorException, BooksDefinitionException, BookDefinitionException;
	
	public ArrayList<Book> getBookList(Module module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException;
	
	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, OpenModuleException;
	
	public LinkedHashMap<String, String> search(Module module, String query, String fromBookID, String toBookID) throws OpenModuleException, CreateModuleErrorException, BookNotFoundException;
	
}
