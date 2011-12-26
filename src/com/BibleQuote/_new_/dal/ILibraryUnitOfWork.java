package com.BibleQuote._new_.dal;

import android.content.Context;

import com.BibleQuote._new_.controllers.CacheModuleController;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IChapterRepository;
import com.BibleQuote._new_.dal.repository.IModuleRepository;

public interface ILibraryUnitOfWork<TModuleId, TModule, TBook> {
	
	public IModuleRepository<TModuleId, TModule> getModuleRepository();
	
	public IBookRepository<TModule, TBook> getBookRepository();
	
	public IChapterRepository<TBook> getChapterRepository();
	
	public Context getLibraryContext();
	
	public CacheModuleController<TModule> getCacheModuleController();
	
}
