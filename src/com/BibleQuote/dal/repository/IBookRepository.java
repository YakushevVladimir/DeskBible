package com.BibleQuote.dal.repository;

import java.util.Collection;
import java.util.LinkedHashMap;

public interface IBookRepository<TModule, TBook> {
    
	/*
	 * Data source related methods
	 * 
	 */
	Collection<TBook> loadBooks(TModule module);
	
//	void insertBook(TBook book);
//    
//	void deleteBook(TBook book);
//	
//	void updateBook(TBook book);
	
	/*
	 * Internal cache related methods
	 *
	 */
	Collection<TBook> getBooks(TModule module);
	
	TBook getBookByID(TModule module, String bookID);
	
	LinkedHashMap<String, String> searchInBook(TModule module, String bookID, String regQuery);

}
