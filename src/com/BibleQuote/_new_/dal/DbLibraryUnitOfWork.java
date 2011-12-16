package com.BibleQuote._new_.dal;


import java.io.File;

import android.content.Context;

import com.BibleQuote._new_.dal.repository.DbBookRepository;
import com.BibleQuote._new_.dal.repository.DbModuleRepository;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IChapterRepository;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.models.DbBook;
import com.BibleQuote._new_.models.DbModule;

public class DbLibraryUnitOfWork  implements ILibraryUnitOfWork<Long, DbModule, DbBook> {
	
    private DbLibraryContext context;
    private IModuleRepository<Long, DbModule> moduleRepository;
    private IBookRepository<DbModule, DbBook> bookRepository;

    public DbLibraryUnitOfWork(File libraryDir, Context context) {
    	this.context = new DbLibraryContext(libraryDir, context);
    }

    public IModuleRepository<Long, DbModule> getModuleRepository()
    {
        if (this.moduleRepository == null)
        {
            this.moduleRepository = new DbModuleRepository(context);
        }
        return this.moduleRepository;
    }

    public IBookRepository<DbModule, DbBook> getBookRepository()
    {
        if (this.bookRepository == null)
        {
            this.bookRepository = new DbBookRepository(context);
        }
        return bookRepository;
    }

	@Override
	public IChapterRepository<DbBook> getChapterRepository() {
		// TODO Auto-generated method stub
		return null;
	}
}
