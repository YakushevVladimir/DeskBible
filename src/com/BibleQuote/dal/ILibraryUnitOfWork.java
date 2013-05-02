package com.BibleQuote.dal;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.dal.repository.IChapterRepository;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.managers.EventManager;

public interface ILibraryUnitOfWork<TModuleId, TModule, TBook> {

	public IModuleRepository<TModuleId, TModule> getModuleRepository();

	public IBookRepository<TModule, TBook> getBookRepository();

	public IChapterRepository<TBook> getChapterRepository();

	public LibraryContext getLibraryContext();

	public CacheModuleController<TModule> getCacheModuleController();

	EventManager getEventManager();
}
