/*
 * Copyright (C) 2011 Scripture Software
 *
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
 * Project: BibleQuote-for-Android
 * File: BQSearchProcessor.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.search;

import android.util.Log;

import com.BibleQuote.dal.repository.BQModuleRepository;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.entity.modules.BQModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BQSearchProcessor {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final String TAG = BQSearchProcessor.class.getSimpleName();

    private final BQModuleRepository repository;
    private Map<String, Map<String, String>> results = Collections.synchronizedMap(new HashMap<String, Map<String, String>>());
    private ExecutorService executor;

    public BQSearchProcessor(BQModuleRepository repository) {
        this.repository = repository;
        this.executor = Executors.newFixedThreadPool(POOL_SIZE);
    }

    /**
     * Выполняет поиск searchQuery, представляющей из себя регулярное выражение в списке книг bookList
     * модуля module.
     *
     * @param module      модуль, в котором необходимо произвести поиск
     * @param bookList    список id книг модуля
     * @param searchQuery искомый текст в виде регулярного выражения
     * @return возвращает словарь, в котором ключами являются ссылки на место в модуле
     * (см. {@linkplain BibleReference}), а значениями полный текст данного места
     */
    public Map<String, String> search(BQModule module, List<String> bookList, String searchQuery) {
        CountDownLatch latch = new CountDownLatch(bookList.size());
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

        Map<String, String> searchRes = new LinkedHashMap<String, String>();
        for (String bookID : bookList) {
            searchRes.putAll(results.get(bookID));
        }

        return searchRes;
    }

    private class SearchThread implements Runnable {
        private CountDownLatch latch;
        private BQModule module;
        private String bookID;
        private String query;

        private SearchThread(CountDownLatch latch, BQModule module, String bookID, String query) {
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