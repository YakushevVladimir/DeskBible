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
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.dal.repository.IChapterRepository;
import com.BibleQuote.dal.repository.IModuleRepository;

public interface ILibraryUnitOfWork<TModuleId, TModule, TBook> {
	
	public IModuleRepository<TModuleId, TModule> getModuleRepository();
	
	public IBookRepository<TModule, TBook> getBookRepository();
	
	public IChapterRepository<TBook> getChapterRepository();
	
	public LibraryContext getLibraryContext();
	
	public CacheModuleController<TModule> getCacheModuleController();
	
}
