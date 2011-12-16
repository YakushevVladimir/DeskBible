package com.BibleQuote._new_.dal;

import java.io.File;

import com.BibleQuote._new_.dal.repository.FsBookRepository;
import com.BibleQuote._new_.dal.repository.FsChapterRepository;
import com.BibleQuote._new_.dal.repository.FsModuleRepository;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IChapterRepository;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;

public class FsLibraryUnitOfWork implements ILibraryUnitOfWork<String, FsModule, FsBook> {

	private FsLibraryContext context;
    private IModuleRepository<String, FsModule> moduleRepository;
    private IBookRepository<FsModule, FsBook> bookRepository;
    private IChapterRepository<FsBook> chapterRepository;

    public FsLibraryUnitOfWork(File libraryDir) {
    	context = new FsLibraryContext(libraryDir);
    }
    
    @Override
    public IModuleRepository<String, FsModule> getModuleRepository()
    {
        if (this.moduleRepository == null)
        {
            this.moduleRepository = new FsModuleRepository(context);
        }
        return this.moduleRepository;
    }

    @Override
    public IBookRepository<FsModule, FsBook> getBookRepository()
    {
        if (this.bookRepository == null)
        {
            this.bookRepository = new FsBookRepository(context);
        }
        return bookRepository;
    }

	@Override
	public IChapterRepository<FsBook> getChapterRepository() {
        if (this.chapterRepository == null)
        {
            this.chapterRepository = new FsChapterRepository(context);
        }
        return chapterRepository;
	}

}
