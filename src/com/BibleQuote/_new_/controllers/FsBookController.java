package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.ModuleNotFoundException;

public class FsBookController implements IBookController {
	//private final String TAG = "FsBookController";

	private IBookRepository<FsModule, FsBook> bRepository;
	private IModuleRepository<String, FsModule> mRepository;

	public FsBookController(FsLibraryUnitOfWork unit) {
		bRepository = unit.getBookRepository();
		mRepository = unit.getModuleRepository();
    }
	
	
	public LinkedHashMap<String, Book> getBooks(Module module) throws ModuleNotFoundException {
		module = getValidModule(module);
		
		LinkedHashMap<String, Book> result = new LinkedHashMap<String, Book>();
		ArrayList<FsBook> bookList = (ArrayList<FsBook>) bRepository.getBooks((FsModule)module);
		if (bookList.size() == 0) {
			bookList = (ArrayList<FsBook>) bRepository.loadBooks((FsModule)module);
		}		
		
		for (Book book : bookList) {
			result.put(book.getID(), book);
		}
		
		return result;		
	}
	
	
	public ArrayList<Book> getBookList(Module module) throws ModuleNotFoundException {
		module = getValidModule(module);

		ArrayList<FsBook> bookList = (ArrayList<FsBook>) bRepository.getBooks((FsModule)module);
		if (bookList.size() == 0) {
			bookList = (ArrayList<FsBook>) bRepository.loadBooks((FsModule)module);
		}
		
		return new ArrayList<Book>(bookList);
	}	
	
	
	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, ModuleNotFoundException {
		module = getValidModule(module);

		Book book = bRepository.getBookByID((FsModule)module, bookID);
		if (book == null) {
			bRepository.loadBooks((FsModule) module);
			book = bRepository.getBookByID((FsModule)module, bookID);
		}
		if (book == null) {
			throw new BookNotFoundException(module.getID(), bookID);
		}		
		return book;
	}		

	
	public LinkedHashMap<String, String> search(Module module, String query, String fromBookID, String toBookID) throws ModuleNotFoundException {
		LinkedHashMap<String, String> searchRes = new LinkedHashMap<String, String>();
	
		if (query.trim().equals("")) {
			// Передана пустая строка
			return searchRes;
		}
		
		// Подготовим регулярное выражение для поиска
		String regQuery = "";
		String[] words = query.toLowerCase().split("\\s+");
		for (String currWord : words) {
			regQuery += (regQuery.equals("") ? "" : "\\s(.)*?") + currWord;
		}
		regQuery = ".*?" + regQuery + ".*?"; // любые символы в начале и конце
	
		boolean startSearch = false;
		for (String bookID : getBooks(module).keySet()) {
			if (!startSearch) {
				startSearch = bookID.equals(fromBookID);
				if (!startSearch) {
					continue;
				}
			} 
			searchRes.putAll(bRepository.searchInBook((FsModule)module, bookID, regQuery));
			if (bookID.equals(toBookID)) {
				break;
			}
		}

		return searchRes;
	}
	
	
	private Module getValidModule(Module module) throws ModuleNotFoundException {
		if (module.getIsClosed()) {
			module = mRepository.loadModuleById((String) module.getDataSourceID());
		}
		String moduleID = module.getID();
		module = mRepository.getModuleByID(moduleID);
		if (module == null) {
			throw new ModuleNotFoundException(moduleID);
		}
		return module;
	}
	
}
