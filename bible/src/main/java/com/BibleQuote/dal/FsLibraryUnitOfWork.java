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
 * Project: BibleQuote-for-Android
 * File: FsLibraryUnitOfWork.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 *
 */

package com.BibleQuote.dal;

import com.BibleQuote.dal.controllers.FsCacheModuleController;
import com.BibleQuote.dal.repository.FsBookRepository;
import com.BibleQuote.dal.repository.FsChapterRepository;
import com.BibleQuote.dal.repository.FsModuleRepository;
import com.BibleQuote.domain.LibraryContext;
import com.BibleQuote.domain.repository.IBookRepository;
import com.BibleQuote.domain.repository.IChapterRepository;
import com.BibleQuote.domain.repository.ILibraryUnitOfWork;
import com.BibleQuote.domain.repository.old.IModuleRepository;
import com.BibleQuote.entity.modules.BQBook;
import com.BibleQuote.entity.modules.BQModule;

public class FsLibraryUnitOfWork implements ILibraryUnitOfWork<String, BQModule, BQBook> {

	private FsLibraryContext libraryContext;
	private IModuleRepository<BQModule> moduleRepository;
	private IBookRepository<BQModule, BQBook> bookRepository;
	private IChapterRepository<BQBook> chapterRepository;
	private FsCacheModuleController<BQModule> cacheModuleController;

	public FsLibraryUnitOfWork(FsLibraryContext fsLibraryContext, FsCacheContext cacheContext) {
		this.libraryContext = fsLibraryContext;
		this.cacheModuleController = new FsCacheModuleController<BQModule>(cacheContext);
	}

	public LibraryContext getLibraryContext() {
		return this.libraryContext;
	}

	public IModuleRepository<BQModule> getModuleRepository() {
		if (this.moduleRepository == null) {
			this.moduleRepository = new FsModuleRepository(libraryContext);
		}
		return this.moduleRepository;
	}

	public IBookRepository<BQModule, BQBook> getBookRepository() {
		if (this.bookRepository == null) {
			this.bookRepository = new FsBookRepository(libraryContext);
		}
		return bookRepository;
	}

	public IChapterRepository<BQBook> getChapterRepository() {
		if (this.chapterRepository == null) {
			this.chapterRepository = new FsChapterRepository(libraryContext);
		}
		return chapterRepository;
	}

	public FsCacheModuleController<BQModule> getCacheModuleController() {
		return this.cacheModuleController;
	}
}
