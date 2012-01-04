package com.BibleQuote.dal;


import android.content.Context;

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

	@Override
	public Context getLibraryContext() {
		return dbLibraryContext.getContext();
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

	@Override
	public IChapterRepository<DbBook> getChapterRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheModuleController<DbModule> getCacheModuleController() {
		// TODO Auto-generated method stub
		return null;
	}


}
