package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote.exceptions.CreateModuleErrorException;

public class FsBookRepository implements IBookRepository<FsModule, FsBook> {
	
	private FsLibraryContext context;
	
    public FsBookRepository(FsLibraryContext context) {
    	this.context = context;
    }
    
    
	@Override
	public Collection<FsBook> loadBooks(FsModule module) {
		if (!context.isModuleLoaded(module)) {
			context.moduleSet.put(module.ShortName, module);
		}
		
		module.Books = context.bookSet = new LinkedHashMap<String, Book>();
		BufferedReader reader = null;
		try {
			reader = context.getModuleReader(module); 
			context.fillBooks(module, reader);
		} catch (CreateModuleErrorException e) {
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
			context.moduleSet.put(module.ShortName, module);
		}
		
		return context.getBookList(module.Books); 
	}

	
	@Override
	public FsBook getBookByName(FsModule module, String bookID) {
		if (!context.isModuleLoaded(module)) {
			context.moduleSet.put(module.ShortName, module);
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
