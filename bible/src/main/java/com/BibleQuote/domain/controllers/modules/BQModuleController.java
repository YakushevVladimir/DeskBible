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
 * File: BQModuleController.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.domain.controllers.modules;

import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.repository.IModuleRepository;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.search.BQSearchProcessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class BQModuleController implements IModuleController {

    private BQModule module;
    private IModuleRepository repository;

    public BQModuleController(BQModule module, IModuleRepository repository) {
        this.module = module;
        this.repository = repository;
    }

    @Override
    public String getModuleId() {
        return module.getID();
    }

    @Override
    public String getModuleName() {
        return module.getName();
    }

    @Override
    public Map<String, String> getBooks() {
        Map<String, Book> books = module.getBooks();
        Map<String, String> result = new LinkedHashMap<String, String>(books.size());
        for (String bookID : books.keySet()) {
            result.put(bookID, books.get(bookID).name);
        }
        return result;
    }

    @Override
    public Chapter getChapter(String book, int chapter) throws BookNotFoundException {
        return repository.loadChapter(module, book, chapter);
    }

    @Override
    public Map<String, String> search(List<String> bookList, String searchQuery) {
        return new BQSearchProcessor(repository).search(module, bookList, searchQuery);
    }
}
