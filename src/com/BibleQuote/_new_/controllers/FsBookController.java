package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Context;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.FsBookRepository;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Module;

public class FsBookController {
	private final String TAG = "FsBookController";
	private FsLibraryUnitOfWork unit;
	private FsBookRepository br;

	public FsBookController(Context context, String libraryPath) {
		unit = new FsLibraryUnitOfWork(context, libraryPath);
		br = unit.getFsBookRepository();
    }
	
	
	public LinkedHashMap<String, Book> loadBooks(Module module) {
		android.util.Log.i(TAG, "Loading books from a file system storage.");
		LinkedHashMap<String, Book> result = new LinkedHashMap<String, Book>();
		
		ArrayList<Book> bookList = new ArrayList<Book>();
		bookList.addAll(br.getBooks(module.getID()));
		for (Book book : bookList) {
			result.put(book.Name, book);
		}
		
		return result;
	}

	
	public ArrayList<Book> getBooks(Module module) {
		ArrayList<Book> books = new ArrayList<Book>();
		if (module.Books == null) {
			module.Books = loadBooks(module);
		}
		if (module.Books != null) {
			for (Book currBook : module.Books.values()) {
				books.add(currBook);
			}
		}
		return books;
	}	
	

	public Book getBook(String moduleShortName) {
		return br.getBookById(moduleShortName);
	}
	
	
	public Book getBook(Module module, String bookID) {
		if (module.Books == null) {
			module.Books = loadBooks(module);
		}
		if (module.Books == null || bookID == null || !module.Books.containsKey(bookID)) {
			return null;
		}
		return module.Books.get(bookID);
	}		
	
	
}
