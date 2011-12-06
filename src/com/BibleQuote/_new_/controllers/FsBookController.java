package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.FsBookRepository;
import com.BibleQuote._new_.models.Book;

public class FsBookController {
	private final String TAG = "FsBookController";
	private FsLibraryUnitOfWork unit;
	private FsBookRepository br;

	public FsBookController(Context context, String libraryPath) {
		unit = new FsLibraryUnitOfWork(context, libraryPath);
		br = unit.getFsBookRepository();
    }
	
	public TreeMap<String, Book> loadBooks(String moduleShortName) {
		android.util.Log.i(TAG, "Loading books from a file system storage.");
		TreeMap<String, Book> result = new TreeMap<String, Book>();
		
		ArrayList<Book> bookList = new ArrayList<Book>();
		bookList.addAll(br.getBooks(moduleShortName));
		for (Book book : bookList) {
			result.put(book.Name, book);
		}
		
		return result;
	}

	public Book getBook(String moduleShortName) {
		return br.getBookById(moduleShortName);
	}
	
}
