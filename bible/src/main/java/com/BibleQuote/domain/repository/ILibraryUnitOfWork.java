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
 * File: ILibraryUnitOfWork.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 *
 */

package com.BibleQuote.domain.repository;

import com.BibleQuote.domain.LibraryContext;
import com.BibleQuote.domain.controllers.ICacheModuleController;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.repository.old.IModuleRepository;

public interface ILibraryUnitOfWork<S extends String, M extends Module, B extends Book> {

    IModuleRepository<M> getModuleRepository();

    IBookRepository<M, B> getBookRepository();

    IChapterRepository<B> getChapterRepository();

    LibraryContext getLibraryContext();

    ICacheModuleController<M> getCacheModuleController();
}
