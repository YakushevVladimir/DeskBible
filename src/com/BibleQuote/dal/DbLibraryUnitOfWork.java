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
import com.BibleQuote.dal.repository.DbBookRepository;
import com.BibleQuote.dal.repository.DbModuleRepository;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.dal.repository.IChapterRepository;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.models.DbBook;
import com.BibleQuote.models.DbModule;

public class DbLibraryUnitOfWork  implements ILibraryUnitOfWork<Long, DbModule, DbBook> {
	
    private DbLibraryContext dbLibraryContext;
    private IModuleRepository<Long, DbModule> moduleRepository;
    private IBookRepository<DbModule, DbBook> bookRepository;

    public DbLibraryUnitOfWork(DbLibraryContext dbLibraryContext) {
    	this.dbLibraryContext = dbLibraryContext;
    }

	public LibraryContext getLibraryContext() {
		return dbLibraryContext;
	}
	
    public IModuleRepository<Long, DbModule> getModuleRepository()
    {
        if (this.moduleRepository == null)
        {
            this.moduleRepository = new DbModuleRepository(dbLibraryContext);
        }
        return this.moduleRepository;
    }

    public IBookRepository<DbModule, DbBook> getBookRepository()
    {
        if (this.bookRepository == null)
        {
            this.bookRepository = new DbBookRepository(dbLibraryContext);
        }
        return bookRepository;
    }

	public IChapterRepository<DbBook> getChapterRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	public CacheModuleController<DbModule> getCacheModuleController() {
		// TODO Auto-generated method stub
		return null;
	}


}
