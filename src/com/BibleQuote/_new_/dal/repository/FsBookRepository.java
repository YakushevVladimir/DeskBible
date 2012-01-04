package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.controllers.CacheModuleController;
import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.Log;

public class FsBookRepository implements IBookRepository<FsModule, FsBook> {
	
	private String TAG = "FsBookRepository";
	private FsLibraryContext context;
	private CacheModuleController<FsModule> cache;
	
    public FsBookRepository(FsLibraryContext context) {
    	this.context = context;
    	this.cache = context.getCache();
    }
    
    
	public Collection<FsBook> loadBooks(FsModule module) {
		module.Books = context.bookSet = new LinkedHashMap<String, Book>();
		BufferedReader reader = null;
		try {
			reader = context.getModuleReader(module); 
			context.fillBooks(module, reader);
			
			// Update cache with just added books
			cache.saveModuleList(context.getModuleList(context.moduleSet));
			
		} catch (CreateModuleErrorException e) {
			Log.e(TAG, "Can't load books by module " + module.getModuleFileName());
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
		
		return context.getBookList(module.Books); 	
	}
	
	
	public Collection<FsBook> getBooks(FsModule module) {
		return context.getBookList(module.Books); 
	}

	
	public FsBook getBookByID(FsModule module, String bookID) {
		return (FsBook)module.Books.get(bookID);
	}


	public LinkedHashMap<String, String> searchInBook(FsModule module, String bookID, String regQuery) {
		FsBook book = getBookByID((FsModule)module, bookID);
		BufferedReader bReader = context.getBookReader(book);
		LinkedHashMap<String, String> searchRes = context.searchInBook(module, bookID, regQuery, bReader);
		
		try {
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		return searchRes;
	}


}
