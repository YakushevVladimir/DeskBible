package com.BibleQuote._new_.dal;


import com.BibleQuote._new_.dal.repository.DbBookRepository;
import com.BibleQuote._new_.dal.repository.DbModuleRepository;

import android.content.Context;

public class DbLibraryUnitOfWork {
	
    private DbLibraryContext context;
    private DbModuleRepository moduleRepository;
    private DbBookRepository bookRepository;

    public DbLibraryUnitOfWork(Context context) {
    	this.context = new DbLibraryContext(context);
    }

    public DbLibraryContext getContext()
    {
    	return this.context;
    }
    
    public DbModuleRepository getDbModuleRepository()
    {
        if (this.moduleRepository == null)
        {
            this.moduleRepository = new DbModuleRepository(context);
        }
        return this.moduleRepository;
    }

    public DbBookRepository getDbBookRepository()
    {
        if (this.bookRepository == null)
        {
            this.bookRepository = new DbBookRepository(context);
        }
        return bookRepository;
    }
}
