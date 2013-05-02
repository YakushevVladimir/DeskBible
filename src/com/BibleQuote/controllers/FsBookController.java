package com.BibleQuote.controllers;

import android.util.Log;
import com.BibleQuote.dal.LibraryUnitOfWork;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.entity.search.SearchProcessor;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.FsBook;
import com.BibleQuote.modules.FsModule;
import com.BibleQuote.modules.Module;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FsBookController implements IBookController {
	private final String TAG = "FsBookController";

	private IBookRepository<FsModule, FsBook> bRepository;
	private IModuleRepository<String, FsModule> mRepository;

	public FsBookController(LibraryUnitOfWork unit) {
		bRepository = unit.getBookRepository();
		mRepository = unit.getModuleRepository();
	}

	public LinkedHashMap<String, Book> getBooks(Module module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		LinkedHashMap<String, Book> result = new LinkedHashMap<String, Book>();
		ArrayList<Book> bookList = getBookList(module);
		for (Book book : bookList) {
			result.put(book.getID(), book);
		}
		return result;
	}

	public ArrayList<Book> getBookList(Module module) throws BooksDefinitionException, BookDefinitionException, OpenModuleException {
		ArrayList<FsBook> bookList = (ArrayList<FsBook>) bRepository.getBooks((FsModule) module);
		if (bookList.size() == 0) {
			bookList = (ArrayList<FsBook>) loadBooks((FsModule) module);
		}
		return new ArrayList<Book>(bookList);
	}

	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, OpenModuleException {
		Book book = bRepository.getBookByID((FsModule) module, bookID);
		if (book == null) {
			try {
				loadBooks((FsModule) module);
			} catch (BooksDefinitionException e) {
				Log.e(TAG, e.getMessage());
			} catch (BookDefinitionException e) {
				Log.e(TAG, e.getMessage());
			}
			book = bRepository.getBookByID((FsModule) module, bookID);
		}
		if (book == null) {
			throw new BookNotFoundException(module.getID(), bookID);
		}
		return book;
	}

	public LinkedHashMap<String, String> search(Module module, String query, String fromBookID, String toBookID)
			throws OpenModuleException, BookNotFoundException {
		return new SearchProcessor(bRepository).search(module, query, getBookList(module, fromBookID, toBookID));
	}

	public ArrayList<String> getBookList(Module module, String fromBookID, String toBookID) throws OpenModuleException {
		ArrayList<String> result = new ArrayList<String>();
		boolean startSearch = false;
		try {
			for (String bookID : getBooks(module).keySet()) {
				if (!startSearch) {
					startSearch = bookID.equals(fromBookID);
					if (!startSearch) continue;
				}
				result.add(bookID);
				if (bookID.equals(toBookID)) break;
			}
		} catch (BooksDefinitionException e) {
			Log.e(TAG, e.getMessage());
		} catch (BookDefinitionException e) {
			Log.e(TAG, e.getMessage());
		}
		return result;
	}

	private ArrayList<FsBook> loadBooks(FsModule module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		ArrayList<FsBook> bookList = null;
		try {
			bookList = (ArrayList<FsBook>) bRepository.loadBooks((FsModule) module);
		} catch (OpenModuleException e) {
			// if the module is bad it will be removed from the collection
			Log.e(TAG, e.getMessage());
			mRepository.deleteModule(module.getID());
			throw new OpenModuleException(module.getID(), module.getDataSourceID());
		}
		return bookList;
	}
}
