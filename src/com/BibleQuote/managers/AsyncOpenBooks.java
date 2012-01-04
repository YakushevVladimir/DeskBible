package com.BibleQuote.managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.listeners.ChangeBooksEvent;
import com.BibleQuote.listeners.IChangeBooksListener.ChangeCode;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.Task;

public class AsyncOpenBooks extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private ChangeBooksEvent event;
	private Librarian librarian;
	private Module module;
	
	public AsyncOpenBooks(String message, Librarian librarian, Module module) {
		super(message);
		this.librarian = librarian;
		this.module = module;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			Log.i(TAG, String.format("Load books for module with moduleID=%1$s", module.getID()));
			ArrayList<Book> bookList = librarian.getBookList(module);
			LinkedHashMap<String, Book> books = new LinkedHashMap<String, Book>();
			for (Book book : bookList) {
				books.put(book.getID(), book);
			}
			event = new ChangeBooksEvent(ChangeCode.BooksAdded, module, books);
		} catch (ModuleNotFoundException e) {
			Log.e(TAG, String.format("AsyncOpenBooks(): ", e.toString()), e);
		}
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	
	public ChangeBooksEvent getEvent() {
		return event;
	}
}
