package com.BibleQuote._new_.dal;

import android.content.Context;

import com.BibleQuote._new_.controllers.CacheModuleController;
import com.BibleQuote._new_.dal.repository.FsBookRepository;
import com.BibleQuote._new_.dal.repository.FsChapterRepository;
import com.BibleQuote._new_.dal.repository.FsModuleRepository;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IChapterRepository;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;

public class FsLibraryUnitOfWork implements ILibraryUnitOfWork<String, FsModule, FsBook> {

	private FsLibraryContext fsLibraryContext;
    private IModuleRepository<String, FsModule> moduleRepository;
    private IBookRepository<FsModule, FsBook> bookRepository;
    private IChapterRepository<FsBook> chapterRepository;
    private CacheModuleController<FsModule> cacheModuleController;
    
    
    public FsLibraryUnitOfWork(FsLibraryContext fsLibraryContext, CacheContext cacheContext) {
    	this.fsLibraryContext = fsLibraryContext;
    	this.cacheModuleController = new CacheModuleController<FsModule>(cacheContext);
    }
    

	public Context getLibraryContext() {
		return this.fsLibraryContext.getContext();
	}
 
    
    public IModuleRepository<String, FsModule> getModuleRepository()
    {
        if (this.moduleRepository == null)
        {
            this.moduleRepository = new FsModuleRepository(fsLibraryContext);
        }
        return this.moduleRepository;
    }

    public IBookRepository<FsModule, FsBook> getBookRepository()
    {
        if (this.bookRepository == null)
        {
            this.bookRepository = new FsBookRepository(fsLibraryContext);
        }
        return bookRepository;
    }

	public IChapterRepository<FsBook> getChapterRepository() {
        if (this.chapterRepository == null)
        {
            this.chapterRepository = new FsChapterRepository(fsLibraryContext);
        }
        return chapterRepository;
	}


	public CacheModuleController<FsModule> getCacheModuleController() {
		return this.cacheModuleController;
	}


}
