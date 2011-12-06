package com.BibleQuote._new_.dal.repository;

import java.util.Collection;

import com.BibleQuote._new_.models.Book;


public interface IBookRepository<T> {
    
	Collection<Book> getBooks(T moduleId);
	
	Book getBookById(T bookId);
	
	void insertBook(Book book);
    
	void deleteBook(T bookId);
	
	void updateBook(Book book);
}
