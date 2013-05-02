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

package com.BibleQuote.entity.search;

import android.util.Log;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.modules.FsBook;
import com.BibleQuote.modules.FsModule;
import com.BibleQuote.modules.Module;

import java.util.*;

public class SearchProcessor {
	private final IBookRepository repository;
	private static final String TAG = "SearchProcessor";
	private Map<String, LinkedHashMap<String, String>> results = Collections.synchronizedMap(new HashMap<String, LinkedHashMap<String, String>>());

	public SearchProcessor(IBookRepository<FsModule, FsBook> repository) {
		this.repository = repository;
	}

	public LinkedHashMap<String, String> search(Module module, String searchQuery, ArrayList<String> bookList) {
		LinkedHashMap<String, String> searchRes = new LinkedHashMap<String, String>();

		ArrayList<SearchThread> threads = new ArrayList<SearchThread>(bookList.size());
		for (String bookID : bookList) {
			SearchThread thread = new SearchThread(module, bookID, searchQuery);
			threads.add(thread);
			thread.start();
		}

		for (SearchThread thread : threads) {
			try {
				thread.join();
				LinkedHashMap<String, String> searches = results.get(thread.getBookID());
				searchRes.putAll(searches);
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		return searchRes;
	}

	private class SearchThread extends Thread {
		Module module;
		String bookID;
		String query;

		public String getBookID() {
			return bookID;
		}

		private SearchThread(Module module, String bookID, String query) {
			this.module = module;
			this.bookID = bookID;
			this.query = query;
		}

		@Override
		public void run() {
			try {
				results.put(bookID, repository.searchInBook(module, bookID, query));
			} catch (BookNotFoundException e) {
				Log.e(TAG, e.getMessage());
				results.put(bookID, new LinkedHashMap<String, String>());
			}
		}
	}
}