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
 * File: MultiThreadSearchProcessor.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.search;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.repository.IModuleRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadSearchProcessor<D, T extends BaseModule> {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int POOL_SIZE = CPU_COUNT * 2 + 1;

    private final IModuleRepository<D, T> repository;
    private ExecutorService executor;

    public MultiThreadSearchProcessor(IModuleRepository<D, T> repository) {
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
     *
     * @return возвращает словарь, в котором ключами являются ссылки на место в модуле
     * (см. {@linkplain BibleReference}), а значениями полный текст данного места
     */
    public Map<String, String> search(T module, List<String> bookList, String searchQuery) {
        Map<String, Future<Map<String, String>>> taskPool = new LinkedHashMap<>();
        for (String bookID : bookList) {
            BookSearchProcessor<D, T> searchProcessor = new BookSearchProcessor<>(repository, module, bookID, searchQuery);
            SearchThread<D, T> thread = new SearchThread<>(searchProcessor);
            taskPool.put(bookID, executor.submit(thread));
        }

        Map<String, String> searchRes = new LinkedHashMap<>();
        for (Map.Entry<String, Future<Map<String, String>>> entry : taskPool.entrySet()) {
            try {
                searchRes.putAll(entry.getValue().get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        return searchRes;
    }

    private static class SearchThread<D, T extends BaseModule> implements Callable<Map<String, String>> {

        private final BookSearchProcessor<D, T> searchProcessor;

        private SearchThread(BookSearchProcessor<D, T> searchProcessor) {
            this.searchProcessor = searchProcessor;
        }

        @Override
        public Map<String, String> call() {
            try {
                return searchProcessor.search();
            } catch (BookNotFoundException e) {
                return new LinkedHashMap<>();
            }
        }
    }
}