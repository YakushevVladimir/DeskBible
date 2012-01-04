package com.BibleQuote._new_.dal;


import android.content.Context;

import com.BibleQuote._new_.controllers.CacheModuleController;
import com.BibleQuote._new_.dal.repository.DbBookRepository;
import com.BibleQuote._new_.dal.repository.DbModuleRepository;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IChapterRepository;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.models.DbBook;
import com.BibleQuote._new_.models.DbModule;

public class DbLibraryUnitOfWork  implements ILibraryUnitOfWork<Long, DbModule, DbBook> {
	
    private DbLibraryContext dbLibraryContext;
    private IModuleRepository<Long, DbModule> moduleRepository;
    private IBookRepository<DbModule, DbBook> bookRepository;

    public DbLibraryUnitOfWork(DbLibraryContext dbLibraryContext) {
    	this.dbLibraryContext = dbLibraryContext;
    }

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

	public IChapterRepository<DbBook> getChapterRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	public CacheModuleController<DbModule> getCacheModuleController() {
		// TODO Auto-generated method stub
		return null;
	}


}
