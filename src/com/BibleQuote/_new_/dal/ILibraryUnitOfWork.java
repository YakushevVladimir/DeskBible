package com.BibleQuote._new_.dal;

import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IChapterRepository;
import com.BibleQuote._new_.dal.repository.IModuleRepository;

public interface ILibraryUnitOfWork<TModuleId, TModule, TBook> {
	
	public IModuleRepository<TModuleId, TModule> getModuleRepository();
	
	public IBookRepository<TModule, TBook> getBookRepository();
	
	public IChapterRepository<TBook> getChapterRepository();
}
