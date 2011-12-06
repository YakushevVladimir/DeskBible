package com.BibleQuote._new_.dal;

import com.BibleQuote._new_.dal.repository.FsBookRepository;
import com.BibleQuote._new_.dal.repository.FsFldModuleRepository;
import com.BibleQuote._new_.dal.repository.FsZipModuleRepository;

import android.content.Context;

public class FsLibraryUnitOfWork {
    private FsLibraryContext context;
    private FsFldModuleRepository moduleRepository;
    private FsZipModuleRepository zipModuleRepository;
    private FsBookRepository bookRepository;

    public FsLibraryUnitOfWork(Context context, String libraryPath) {
    	this.context = new FsLibraryContext(context, libraryPath);
    }

    public FsLibraryContext getContext()
    {
    	return this.context;
    }
    
    public FsFldModuleRepository getFsModuleRepository()
    {
        if (this.moduleRepository == null)
        {
            this.moduleRepository = new FsFldModuleRepository(context);
        }
        return this.moduleRepository;
    }

    public FsZipModuleRepository getFsZipModuleRepository()
    {
        if (this.zipModuleRepository == null)
        {
            this.zipModuleRepository = new FsZipModuleRepository(context);
        }
        return this.zipModuleRepository;
    }
    
    public FsBookRepository getFsBookRepository()
    {
        if (this.bookRepository == null)
        {
            this.bookRepository = new FsBookRepository(context);
        }
        return bookRepository;
    }
}
