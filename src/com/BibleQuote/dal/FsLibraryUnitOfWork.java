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

package com.BibleQuote.dal;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.repository.FsBookRepository;
import com.BibleQuote.dal.repository.FsChapterRepository;
import com.BibleQuote.dal.repository.FsModuleRepository;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.dal.repository.IChapterRepository;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.models.FsBook;
import com.BibleQuote.models.FsModule;

public class FsLibraryUnitOfWork implements ILibraryUnitOfWork<String, FsModule, FsBook> {

    private FsLibraryContext fsLibraryContext;
    private IModuleRepository<String, FsModule> moduleRepository;
    private IBookRepository<FsModule, FsBook> bookRepository;
    private IChapterRepository<FsBook> chapterRepository;
    private CacheModuleController<FsModule> cacheModuleController;


    public FsLibraryUnitOfWork(FsLibraryContext fsLibraryContext, CacheContext cacheContext) {
        this.fsLibraryContext = fsLibraryContext;
        this.cacheModuleController = new CacheModuleController<FsModule>(cacheContext);
    }


    public LibraryContext getLibraryContext() {
        return this.fsLibraryContext;
    }


    public IModuleRepository<String, FsModule> getModuleRepository() {
        if (this.moduleRepository == null) {
            this.moduleRepository = new FsModuleRepository(fsLibraryContext);
        }
        return this.moduleRepository;
    }

    public IBookRepository<FsModule, FsBook> getBookRepository() {
        if (this.bookRepository == null) {
            this.bookRepository = new FsBookRepository(fsLibraryContext);
        }
        return bookRepository;
    }

    public IChapterRepository<FsBook> getChapterRepository() {
        if (this.chapterRepository == null) {
            this.chapterRepository = new FsChapterRepository(fsLibraryContext);
        }
        return chapterRepository;
    }


    public CacheModuleController<FsModule> getCacheModuleController() {
        return this.cacheModuleController;
    }


}
