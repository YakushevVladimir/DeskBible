/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: FsBookController.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.controllers;

import com.BibleQuote.dal.FsLibraryUnitOfWork;
import com.BibleQuote.domain.controllers.IBookController;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.repository.IBookRepository;
import com.BibleQuote.domain.repository.old.IModuleRepository;
import com.BibleQuote.entity.modules.BQBook;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.search.SearchProcessor;
import com.BibleQuote.utils.Log;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class FsBookController implements IBookController {
	private static final String TAG = "FsBookController";

	private IBookRepository<BQModule, BQBook> bRepository;
	private IModuleRepository<String, BQModule> mRepository;

	public FsBookController(FsLibraryUnitOfWork unit) {
		bRepository = unit.getBookRepository();
		mRepository = unit.getModuleRepository();
	}

	public ArrayList<Book> getBookList(Module module) throws BooksDefinitionException, BookDefinitionException, OpenModuleException {
		ArrayList<BQBook> bookList = (ArrayList<BQBook>) bRepository.getBooks((BQModule) module);
		if (bookList.size() == 0) {
			bookList = loadBooks((BQModule) module);
		}
		return new ArrayList<Book>(bookList);
	}

	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, OpenModuleException {
		Book book = bRepository.getBookByID((BQModule) module, bookID);
		if (book == null) {
			try {
				loadBooks((BQModule) module);
			} catch (BooksDefinitionException e) {
				Log.e(TAG, e.getMessage());
			} catch (BookDefinitionException e) {
				Log.e(TAG, e.getMessage());
			}
			book = bRepository.getBookByID((BQModule) module, bookID);
		}
		if (book == null) {
			throw new BookNotFoundException(module.getID(), bookID);
		}
		return book;
	}

	public Map<String, String> search(Module module, String query, String fromBookID, String toBookID)
			throws OpenModuleException, BookNotFoundException {
		Log.i(TAG, String.format("Start search to word '%s' from book '%s' to book '%s'", query, fromBookID, toBookID));

		long startTime = System.currentTimeMillis();
		Map<String, String> result = new SearchProcessor(bRepository)
				.search(module, module.getBookList(fromBookID, toBookID), query);
		Log.i(TAG, String.format(Locale.getDefault(), "Search time: %d ms", (System.currentTimeMillis() - startTime)));

		return result;
	}

	private ArrayList<BQBook> loadBooks(BQModule module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		ArrayList<BQBook> bookList;
		try {
			bookList = (ArrayList<BQBook>) bRepository.loadBooks(module);
		} catch (OpenModuleException e) {
			// if the module is bad it will be removed from the collection
			Log.e(TAG, e.getMessage());
			mRepository.deleteModule(module.getID());
			throw new OpenModuleException(module.getID(), module.getDataSourceID());
		}
		return bookList;
	}
}
