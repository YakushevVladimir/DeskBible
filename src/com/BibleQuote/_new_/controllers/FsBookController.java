package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.os.AsyncTask;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.listeners.ChangeBooksEvent;
import com.BibleQuote._new_.listeners.IChangeBooksListener.ChangeCode;
import com.BibleQuote._new_.managers.EventManager;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;

public class FsBookController implements IBookController {
	private final String TAG = "FsBookController";

	private EventManager eventManager;
	private IBookRepository<FsModule, FsBook> br;
	

	public FsBookController(FsLibraryUnitOfWork unit, EventManager eventManager) {
		this.eventManager = eventManager;
		br = unit.getBookRepository();
    }
	
	
	public LinkedHashMap<String, Book> loadBooks(Module module) {
		android.util.Log.i(TAG, "Loading books from a file system storage.");
		LinkedHashMap<String, Book> result = new LinkedHashMap<String, Book>();

		ArrayList<Book> bookList = new ArrayList<Book>();
		bookList.addAll(br.loadBooks((FsModule)module));
		for (Book book : bookList) {
			result.put(book.Name, book);
		}
		
		return result;
	}

	
	public void loadBooksAsync(Module module) {
		new LoadBooksAsync().execute(module);
	}
	
	
	public LinkedHashMap<String, Book> getBooks(Module module) {
		ArrayList<FsBook> bookList = (ArrayList<FsBook>) br.getBooks((FsModule)module);
		if (bookList.size() == 0) {
			bookList = (ArrayList<FsBook>) br.loadBooks((FsModule)module);
		}		
		
		LinkedHashMap<String, Book> result = new LinkedHashMap<String, Book>();
		for (Book book : bookList) {
			result.put(book.Name, book);
		}
		
		return result;		
	}
	
	
	public ArrayList<Book> getBookList(Module module) {
		ArrayList<FsBook> bookList = (ArrayList<FsBook>) br.getBooks((FsModule)module);
		if (bookList.size() == 0) {
			bookList = (ArrayList<FsBook>) br.loadBooks((FsModule)module);
		}
		
		return new ArrayList<Book>(bookList);
	}	
	
	
	public Book getBook(Module module, String bookID) {
		Book book = br.getBookByName((FsModule)module, bookID);
		if (book == null) {
			br.loadBooks((FsModule)module);
			book = br.getBookByName((FsModule)module, bookID);
		}
		return book;
	}		
	
	
	private class LoadBooksAsync extends AsyncTask<Module, Void, LinkedHashMap<String, Book>> {
		@Override
		protected void onPostExecute(LinkedHashMap<String, Book> result) {
			super.onPostExecute(result);
			
			ChangeBooksEvent event = new ChangeBooksEvent(ChangeCode.BooksLoaded, null, result);
			eventManager.fireChangeBooksEvent(event);
		}

		@Override
		protected LinkedHashMap<String, Book> doInBackground(Module... params) {
			return loadBooks(params[0]);
		}
	}
	
	
}
