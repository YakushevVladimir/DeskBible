package com.BibleQuote.dal;

import com.BibleQuote.dal.repository.IChapterRepository;
import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.repository.IBookRepository;
import com.BibleQuote.dal.repository.IModuleRepository;

public interface ILibraryUnitOfWork<T, S, U> {

	public IModuleRepository<S> getModuleRepository();

	public IBookRepository<S, U> getBookRepository();

	public IChapterRepository<U> getChapterRepository();

	public LibraryContext getLibraryContext();

	public CacheModuleController<S> getCacheModuleController();
}
