package com.BibleQuote.controllers;

import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Module;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface IBookController {

	public LinkedHashMap<String, Book> getBooks(Module module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException;

	/**
	 * Возвращает коллекцию Book для указанного модуля. Данные о книгах в первую
	 * очередь берутся из контекста библиотеки. Если там для выбранного модуля
	 * список книг отсутсвует, то производится загрузка коллекции Book из хранилища
	 *
	 * @param module модуль для которого необходимо получить коллекцию Book
	 * @return коллекцию Book для указанного модуля
	 * @throws OpenModuleException
	 * @throws BooksDefinitionException
	 * @throws BookDefinitionException
	 */
	public ArrayList<Book> getBookList(Module module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException;

	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, OpenModuleException;

	public LinkedHashMap<String, String> search(Module module, String query, String fromBookID, String toBookID) throws OpenModuleException, BookNotFoundException;

}
