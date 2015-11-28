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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchProcessor {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final String TAG = SearchProcessor.class.getSimpleName();

    private final IBookRepository repository;
	private Map<String, LinkedHashMap<String, String>> results = Collections.synchronizedMap(new HashMap<String, LinkedHashMap<String, String>>());
    private ExecutorService executor;

    public SearchProcessor(IBookRepository<FsModule, FsBook> repository) {
		this.repository = repository;
        this.executor = Executors.newFixedThreadPool(POOL_SIZE);
	}

    /**
     * Выполняет поиск searchQuery, представляющей из себя регулярное выражение в списке книг bookList
     * модуля module.
     *
     * @param module модуль, в котором необходимо произвести поиск
     * @param bookList список id книг модуля
     * @param searchQuery искомый текст в виде регулярного выражения
     *
     * @return возвращает словарь, в котором ключами являются ссылки на место в модуле
     *         (см. {@linkplain com.BibleQuote.entity.BibleReference}), а значениями полный текст данного места
     */
    public LinkedHashMap<String, String> search(Module module, ArrayList<String> bookList, String searchQuery) {
        CountDownLatch latch =  new CountDownLatch(bookList.size());
		for (String bookID : bookList) {
			SearchThread thread = new SearchThread(latch, module, bookID, searchQuery);
            executor.execute(thread);
		}
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LinkedHashMap<String, String> searchRes = new LinkedHashMap<String, String>();
        for (String bookID : bookList) {
            LinkedHashMap<String, String> searches = results.get(bookID);
            searchRes.putAll(searches);
        }

		return searchRes;
	}

	private class SearchThread implements Runnable {
        private CountDownLatch latch;
        private Module module;
        private String bookID;
        private String query;

		public String getBookID() {
			return bookID;
		}

		private SearchThread(CountDownLatch latch, Module module, String bookID, String query) {
            this.latch = latch;
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
            latch.countDown();
		}
	}
}