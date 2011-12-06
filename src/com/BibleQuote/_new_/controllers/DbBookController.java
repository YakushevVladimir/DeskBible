package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;

import com.BibleQuote._new_.dal.DbLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.DbBookRepository;
import com.BibleQuote._new_.models.Book;

public class DbBookController {
	private final String TAG = "DbBookController";
	private DbLibraryUnitOfWork unit;
	private DbBookRepository br;
	
    public DbBookController(Context context)
    {
    	unit = new DbLibraryUnitOfWork(context);
		br = unit.getDbBookRepository();
    }

    
	public TreeMap<String, Book> loadBooks(Long moduleId) {
		android.util.Log.i(TAG, "Loading books from a DB storage.");
		TreeMap<String, Book> result = new TreeMap<String, Book>();
		
		ArrayList<Book> bookList = new ArrayList<Book>();
		bookList.addAll(br.getBooks(moduleId));
		for (Book book : bookList) {
			result.put(book.Name, book);
		}
		
		return result;
	}
	
	public Book getBook(long bookId) {
		return br.getBookById(bookId);
	}
    
}
