package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.controllers.CacheModuleController;
import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
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
    
    
	@Override
	public Collection<FsBook> loadBooks(FsModule module) {
		if (!context.isModuleLoaded(module)) {
			context.moduleSet.put(module.getID(), module);
		}
		
		module.Books = context.bookSet = new LinkedHashMap<String, Book>();
		context.chapterSet = new LinkedHashMap<Integer, Chapter>();
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
				reader.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
		
		return context.getBookList(module.Books); 	
	}
	
	
	@Override
	public Collection<FsBook> getBooks(FsModule module) {
		if (!context.isModuleLoaded(module)) {
			context.moduleSet.put(module.getID(), module);
		}
		
		return context.getBookList(module.Books); 
	}

	
	@Override
	public FsBook getBookByID(FsModule module, String bookID) {
		if (!context.isModuleLoaded(module)) {
			context.moduleSet.put(module.getID(), module);
		}
		
		return (FsBook)module.Books.get(bookID);
	}

	
	@Override
	public void insertBook(FsBook book) {
	}

	
	@Override
	public void deleteBook(FsBook book) {
	}


	@Override
	public void updateBook(FsBook book) {
	}


}
