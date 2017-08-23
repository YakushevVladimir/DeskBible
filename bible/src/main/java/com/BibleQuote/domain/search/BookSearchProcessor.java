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
 * File: BookSearchProcessor.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.search;

import android.support.annotation.NonNull;

import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.repository.IModuleRepository;
import com.BibleQuote.domain.search.algorithm.BoyerMoorAlgorithm;
import com.BibleQuote.domain.search.algorithm.SearchAlgorithm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class BookSearchProcessor<D, T extends Module> {

    private final Map<String, String> result = new LinkedHashMap<>();
    private final IModuleRepository<D, T> repository;
    private T module;
    private String bookID;
    private String query;
    private String[] words;
    private Map<String, SearchAlgorithm> algoritms = new HashMap<>();

    BookSearchProcessor(IModuleRepository<D, T> repository, T module, String bookID, String query) {
        this.repository = repository;
        this.module = module;
        this.bookID = bookID;
        this.query = query.trim();
    }

    @NonNull
    public Map<String, String> search() throws BookNotFoundException {
        if (query == null || query.isEmpty()) {
            return result;
        }


        String bookContent = repository.getBookContent(module, bookID);

        words = query.trim().split("\\s+");

        // данная проверка позволяет сэкономить время на делении контента на главы и стихи
        for (String word : words) {
            BoyerMoorAlgorithm algorithm = new BoyerMoorAlgorithm(word);
            if (algorithm.indexOf(bookContent) == -1) {
                return result;
            }
            algoritms.put(word, algorithm);
        }

        int chapter = module.isChapterZero() ? 0 : 1;
        String chapterSign = module.getChapterSign();
        SearchAlgorithm chapterAlgorithm = new BoyerMoorAlgorithm(chapterSign);
        int indexStart = chapterAlgorithm.indexOf(bookContent);
        while (indexStart != -1) {
            int indexEnd = chapterAlgorithm.indexOf(bookContent, indexStart + chapterSign.length());
            searchInChapter(bookContent, indexStart, indexEnd, chapter);
            indexStart = indexEnd;
            chapter++;
        }

        return result;
    }

    private void searchInChapter(String bookContent, int start, int end, int chapter) {
        int verse = 1;
        String verseSign = module.getVerseSign();
        SearchAlgorithm verseAlgorithm = new BoyerMoorAlgorithm(verseSign);
        int indexStart = verseAlgorithm.indexOf(bookContent, start, end);
        while (indexStart != -1) {
            int indexEnd = verseAlgorithm.indexOf(bookContent, indexStart + verseSign.length(), end);
            searchInVerse(bookContent, indexStart, indexEnd == -1 ? end : indexEnd, chapter, verse);
            indexStart = indexEnd;
            verse++;
        }
    }

    private void searchInVerse(String bookContent, int start, int end, int chapter, int verse) {
        String moduleId = module.getID();
        int offset = start;
        for (String word : words) {
            SearchAlgorithm algorithm = algoritms.get(word);
            offset = algorithm.indexOf(bookContent, offset, end);
            if (offset == -1) {
                return;
            }
            offset += word.length();
        }
        result.put(new BibleReference(moduleId, bookID, chapter, verse).getPath(),
                bookContent.substring(start, end == -1 ? bookContent.length() : end));
    }

}
