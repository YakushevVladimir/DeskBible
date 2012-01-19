package com.BibleQuote.managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.listeners.ChangeBooksEvent;
import com.BibleQuote.listeners.IChangeBooksListener.ChangeCode;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OSISLink;
import com.BibleQuote.utils.Task;

public class AsyncOpenBooks extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private ChangeBooksEvent event;
	private Librarian librarian;
	private OSISLink link;
	
	public AsyncOpenBooks(String message, Boolean isHidden, Librarian librarian, OSISLink link) {
		super(message, isHidden);
		this.librarian = librarian;
		this.link = link;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			Log.i(TAG, String.format("Open OSIS link with moduleID=%1$s", link.getModuleID()));
			Module module = librarian.openModule(link.getModuleID(), link.getModuleDatasourceID());
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
