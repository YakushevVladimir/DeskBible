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
 * File: FsBookRepository.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.repository;

import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.repository.IBookRepository;
import com.BibleQuote.entity.modules.BQBook;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

public class FsBookRepository implements IBookRepository<BQModule, BQBook> {

	private static final String TAG = FsBookRepository.class.getSimpleName();

    private FsLibraryContext context;

	public FsBookRepository(FsLibraryContext context) {
		this.context = context;
	}

	public Collection<BQBook> loadBooks(BQModule module)
			throws OpenModuleException, BooksDefinitionException, BookDefinitionException {

		synchronized (FsBookRepository.class) {

			if (module.getBooks() != context.bookSet) {

				module.setBooks(context.bookSet = new LinkedHashMap<String, Book>());
				BufferedReader reader = null;
				String moduleID = "";
				String moduleDatasourceID = "";
				try {
					moduleID = module.getID();
					moduleDatasourceID = module.getDataSourceID();
					reader = context.getModuleReader(module);
					context.fillBooks(module, reader);

				} catch (DataAccessException e) {
					Log.e(TAG, String.format("Can't load books from module (%1$s, %2$s)", moduleID, moduleDatasourceID));
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

	public Collection<BQBook> getBooks(BQModule module) {
		return context.getBookList(
				module.getBooks() == context.bookSet
						? context.bookSet : null);
	}

	public BQBook getBookByID(BQModule module, String bookID) {
		return module.getBooks() == context.bookSet
				? (BQBook) context.bookSet.get(bookID) : null;
	}

	public LinkedHashMap<String, String> searchInBook(BQModule module, String bookID, String regQuery) throws BookNotFoundException {
		LinkedHashMap<String, String> searchRes = null;

		BQBook book = getBookByID(module, bookID);
		if (book == null) {
			throw new BookNotFoundException(module.getID(), bookID);
		}

		BufferedReader bReader = null;
		try {
			bReader = context.getBookReader(book);
			searchRes = context.searchInBook(module, bookID, regQuery, bReader);
		} catch (DataAccessException e) {
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
