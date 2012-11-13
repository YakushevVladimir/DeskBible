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

package com.BibleQuote.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote.dal.DbLibraryUnitOfWork;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.managers.EventManager;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.DbBook;
import com.BibleQuote.models.DbModule;
import com.BibleQuote.models.Module;

public class DbBookController implements IBookController {
	//private final String TAG = "DbBookController";
	
	//private EventManager eventManager;
	private IBookRepository<DbModule, DbBook> br;
	
    public DbBookController(DbLibraryUnitOfWork unit, EventManager eventManager)
    {
		//this.eventManager = eventManager;
		br = unit.getBookRepository();
    }

    
//	public LinkedHashMap<String, Book> loadBooks(Module module) {
//		android.util.Log.i(TAG, "Loading books from a DB storage.");
//		LinkedHashMap<String, Book> result = new LinkedHashMap<String, Book>();
//		
//		ArrayList<Book> bookList = new ArrayList<Book>();
//		bookList.addAll(br.loadBooks((DbModule)module));
//		for (Book book : bookList) {
//			result.put(book.getID(), book);
//		}
//		
//		return result;
//	}

	public LinkedHashMap<String, Book> getBooks(Module module) {
		// TODO Auto-generated method stub
		return null;
	}


	public ArrayList<Book> getBookList(Module module) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Book getBookByID(Module module, String bookName) {
		return br.getBookByID((DbModule)module, bookName);
	}


	public LinkedHashMap<String, String> search(Module module, String query,
			String fromBookID, String toBookID) {
		// TODO Auto-generated method stub
		return null;
	}

}
