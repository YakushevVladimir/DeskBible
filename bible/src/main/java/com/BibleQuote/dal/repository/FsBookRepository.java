package com.BibleQuote.dal.repository;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.exceptions.*;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.FsBook;
import com.BibleQuote.modules.FsModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

public class FsBookRepository implements IBookRepository<FsModule, FsBook> {

	//private String TAG = "FsBookRepository";
	private FsLibraryContext context;
	private CacheModuleController<FsModule> cache;

	public FsBookRepository(FsLibraryContext context) {
		this.context = context;
		this.cache = context.getCache();
	}

	public Collection<FsBook> loadBooks(FsModule module)
			throws OpenModuleException, BooksDefinitionException, BookDefinitionException {

		synchronized (context.bookSet) {

			if (module.Books != context.bookSet) {

				module.Books = context.bookSet = new LinkedHashMap<String, Book>();
				BufferedReader reader = null;
				String moduleID = "";
				String moduleDatasourceID = "";
				try {
					moduleID = module.getID();
					moduleDatasourceID = module.getDataSourceID();
					reader = context.getModuleReader(module);
					context.fillBooks(module, reader);

				} catch (FileAccessException e) {
					//Log.e(TAG, String.format("Can't load books from module (%1$s, %2$s)", moduleID, moduleDatasourceID));
					throw new OpenModuleException(moduleID, moduleDatasourceID);

				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return context.getBookList(context.bookSet);
		}
	}

	public Collection<FsBook> getBooks(FsModule module) {
		return context.getBookList(
				module.Books == context.bookSet
						? context.bookSet : null);
	}

	public FsBook getBookByID(FsModule module, String bookID) {
		return module.Books == context.bookSet
				? (FsBook) context.bookSet.get(bookID) : null;
	}

	public LinkedHashMap<String, String> searchInBook(FsModule module, String bookID, String regQuery) throws BookNotFoundException {
		LinkedHashMap<String, String> searchRes = null;

		FsBook book = getBookByID(module, bookID);
		if (book == null) {
			throw new BookNotFoundException(module.getID(), bookID);
		}

		BufferedReader bReader = null;
		try {
			bReader = context.getBookReader(book);
			searchRes = context.searchInBook(module, bookID, regQuery, bReader);
		} catch (FileAccessException e) {
			throw new BookNotFoundException(module.getID(), bookID);

		} finally {
			try {
				if (bReader != null) {
					bReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return searchRes;
	}
}
