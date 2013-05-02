package com.BibleQuote.dal;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.repository.*;
import com.BibleQuote.managers.EventManager;
import com.BibleQuote.modules.FsBook;
import com.BibleQuote.modules.FsModule;

public class LibraryUnitOfWork implements ILibraryUnitOfWork<String, FsModule, FsBook> {

	private FsLibraryContext libraryContext;
	private IModuleRepository<String, FsModule> moduleRepository;
	private IBookRepository<FsModule, FsBook> bookRepository;
	private IChapterRepository<FsBook> chapterRepository;
	private CacheModuleController<FsModule> cacheModuleController;

	public LibraryUnitOfWork(FsLibraryContext fsLibraryContext, CacheContext cacheContext) {
		this.libraryContext = fsLibraryContext;
		this.cacheModuleController = new CacheModuleController<FsModule>(cacheContext);
	}

	public LibraryContext getLibraryContext() {
		return this.libraryContext;
	}

	public IModuleRepository<String, FsModule> getModuleRepository() {
		if (this.moduleRepository == null) {
			this.moduleRepository = new FsModuleRepository(libraryContext);
		}
		return this.moduleRepository;
	}

	public IBookRepository<FsModule, FsBook> getBookRepository() {
		if (this.bookRepository == null) {
			this.bookRepository = new FsBookRepository(libraryContext);
		}
		return bookRepository;
	}

	public IChapterRepository<FsBook> getChapterRepository() {
		if (this.chapterRepository == null) {
			this.chapterRepository = new FsChapterRepository(libraryContext);
		}
		return chapterRepository;
	}

	public CacheModuleController<FsModule> getCacheModuleController() {
		return this.cacheModuleController;
	}

	@Override
	public EventManager getEventManager() {
		return libraryContext.eventManager;
	}
}
