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
 * File: BQModuleRepository.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.repository;

import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.repository.IModuleRepository;
import com.BibleQuote.entity.modules.BQBook;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.CachePool;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class BQModuleRepository implements IModuleRepository<BQModule> {

    public static final String TAG = BQModuleRepository.class.getSimpleName();
    public static final String INI_FILENAME = "bibleqt.ini";

    public Map<String, Chapter> chapterPool = Collections.synchronizedMap(new CachePool<Chapter>());

    private FsLibraryContext context;

    public BQModuleRepository(FsLibraryContext context) {
        this.context = context;
    }

    @Override
    public BQModule loadModule(String dataSourceID) throws OpenModuleException {
        if (dataSourceID.endsWith("zip")) {
            dataSourceID = dataSourceID + File.separator + INI_FILENAME;
        }

        BQModule result = null;
        try {
            result = new BQModule(dataSourceID);
            result.defaultEncoding = context.getModuleEncoding(getModuleReader(result));
            context.fillModule(result, getModuleReader(result));
        } catch (DataAccessException e) {
            Log.i(TAG, "!!!..Error open module from " + dataSourceID);
            throw new OpenModuleException(dataSourceID, result.modulePath);
        }
        return result;
    }

    @Override
    public Chapter loadChapter(BQModule module, String bookID, int chapter) throws BookNotFoundException {
        String chapterID = getChapterID(module, bookID, chapter);
        if (chapterPool.containsKey(chapterID)) {
            return chapterPool.get(chapterID);
        }

        Chapter result = null;
        BufferedReader reader = null;
        try {
            Book book = module.getBook(bookID);
            reader = getBookReader(module, book);
            result = context.loadChapter(book, chapter, reader);
            chapterPool.put(chapterID, result);
        } catch (DataAccessException e) {
            Log.e(TAG, "Can't load chapters of book with ID = " + bookID, e);
            throw new BookNotFoundException(module.getID(), bookID);

        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public LinkedHashMap<String, String> searchInBook(BQModule module, String bookID, String regQuery) throws BookNotFoundException {
        BQBook book = (BQBook) module.getBook(bookID);
        if (book == null) {
            throw new BookNotFoundException(module.getID(), bookID);
        }

        LinkedHashMap<String, String> searchRes = null;
        BufferedReader bReader = null;
        try {
            bReader = getBookReader(module, book);
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

    private BufferedReader getBookReader(BQModule module, Book book) throws DataAccessException {
        return module.isArchive()
                ? FsUtils.getTextFileReaderFromZipArchive(module.modulePath, book.getDataSourceID(), module.defaultEncoding)
                : FsUtils.getTextFileReader(module.modulePath, book.getDataSourceID(), module.defaultEncoding);
    }

    private String getChapterID(Module module, String bookID, int chapter) {
        return String.format("%s:%s:%s", module.getID(), bookID, chapter);
    }

    private BufferedReader getModuleReader(BQModule module) throws DataAccessException {
        return module.isArchive()
                ? FsUtils.getTextFileReaderFromZipArchive(module.modulePath, module.iniFileName, module.defaultEncoding)
                : FsUtils.getTextFileReader(module.modulePath, module.iniFileName, module.defaultEncoding);
    }
}
