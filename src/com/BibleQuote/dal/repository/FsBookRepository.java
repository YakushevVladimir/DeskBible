/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.FsBook;
import com.BibleQuote.models.FsModule;

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
		
				// Update cache with just added books
				cache.saveModuleList(context.getModuleList(context.moduleSet));
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
				? (FsBook)context.bookSet.get(bookID) : null;
	}


	public LinkedHashMap<String, String> searchInBook(FsModule module, String bookID, String regQuery) throws BookNotFoundException {
		LinkedHashMap<String, String> searchRes = null;
		BufferedReader bReader = null;
		String moduleID = module.getID();
		
		FsBook book = getBookByID((FsModule)module, bookID);
		if (book == null) {
			//Log.e(TAG, "Can't load books from module with ID=" + moduleID);
			throw new BookNotFoundException(moduleID, bookID);
		}
		
		try {
			bReader = context.getBookReader(book);
			searchRes = context.searchInBook(module, bookID, regQuery, bReader);
		} catch (FileAccessException e) {
			//Log.e(TAG, "Can't load books from module with ID=" + moduleID);
			throw new BookNotFoundException(moduleID, bookID);
			
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
