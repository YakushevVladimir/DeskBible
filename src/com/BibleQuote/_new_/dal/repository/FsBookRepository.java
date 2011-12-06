package com.BibleQuote._new_.dal.repository;

import java.util.Collection;

import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.Book;

public class FsBookRepository implements IBookRepository<String> {
	
	FsLibraryContext context;

    public FsBookRepository(FsLibraryContext context)
    {
    	this.context = context;
    }
    
	@Override
	public Collection<Book> getBooks(String moduleId) {
		return null;
	}

	@Override
	public Book getBookById(String bookId) {
		return null;
	}

	
	@Override
	public void insertBook(Book book) {
	}

	
	@Override
	public void deleteBook(String bookId) {
	}
	

	@Override
	public void updateBook(Book book) {
	}

}
