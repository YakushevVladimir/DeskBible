package com.BibleQuote.dal;


import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.repository.*;
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
