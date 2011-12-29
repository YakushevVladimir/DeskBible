package com.BibleQuote._new_.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IBookRepository;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.utils.FileUtilities;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.StringProc;

public class FsBookController implements IBookController {
	private final String TAG = "FsBookController";

	private IBookRepository<FsModule, FsBook> bRepository;
	private IModuleRepository<String, FsModule> mRepository;

	public FsBookController(FsLibraryUnitOfWork unit) {
		bRepository = unit.getBookRepository();
		mRepository = unit.getModuleRepository();
    }
	
	
	public LinkedHashMap<String, Book> getBooks(Module module) {
		module = getValidModule(module);
		if (module == null) {
			return new LinkedHashMap<String, Book>();
		}
		
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
	
	
	public ArrayList<Book> getBookList(Module module) {
		module = getValidModule(module);
		if (module == null) {
			return new ArrayList<Book>();
		}

		ArrayList<FsBook> bookList = (ArrayList<FsBook>) bRepository.getBooks((FsModule)module);
		if (bookList.size() == 0) {
			bookList = (ArrayList<FsBook>) bRepository.loadBooks((FsModule)module);
		}
		
		return new ArrayList<Book>(bookList);
	}	
	
	
	public Book getBook(Module module, String bookID) {
		module = getValidModule(module);
		if (module == null) {
			return null;
		}

		Book book = bRepository.getBookByID((FsModule)module, bookID);
		if (book == null) {
			bRepository.loadBooks((FsModule) module);
			book = bRepository.getBookByID((FsModule)module, bookID);
		}
		return book;
	}		

	
	public LinkedHashMap<String, String> search(Module module, String query, String fromBookID, String toBookID) {
		LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
	
		if (query.trim().equals("")) {
			// Передана пустая строка
			return ret;
		}
		
		LinkedHashMap<String, String> SearchRes = new LinkedHashMap<String, String>();
		// Подготовим регулярное выражение для поиска
		String regQuery = "";
		String[] words = query.toLowerCase().split("\\s+");
		for (String currWord : words) {
			regQuery += (regQuery.equals("") ? "" : "\\s(.)*?") + currWord;
		}
		regQuery = ".*?" + regQuery + ".*?"; // любые символы в начале и конце
	
		SearchRes.clear();
		
		boolean startSearch = false;
		for (String bookID : getBooks(module).keySet()) {
			if (!startSearch) {
				startSearch = bookID.equals(fromBookID);
				if (!startSearch) {
					continue;
				}
			} 
			searchInBook(module, bookID, regQuery, SearchRes);
			if (bookID.equals(toBookID)) {
				break;
			}
		}
		return SearchRes;
	}
	
	
	private void searchInBook(Module module, String bookID, String regQuery, LinkedHashMap<String, String> SearchRes) {
		
		Book book = bRepository.getBookByID((FsModule)module, bookID);

		// TODO: Получение BufferedReader некорректное. Работает только с несжатыми модулями 
		BufferedReader bReader = getReader(
				((FsModule)module).modulePath, 
				((FsBook)book).getDataSourceID(),
				module.defaultEncoding);
		if (bReader == null) {
			return;
		}

		String str;
		int chapter = module.ChapterZero ? -1 : 0;
		int verse = 0;
		try {
			while ((str = bReader.readLine()) != null) {
				str = str.replaceAll("\\s(\\d)+", "");
				if (str.toLowerCase().contains(module.ChapterSign)) {
					chapter++;
					verse = 0;
				}
				if (str.toLowerCase().contains(module.VerseSign))
					verse++;

				if (str.toLowerCase().matches(regQuery)) {
					String linkOSIS = module.ShortName + "." + bookID + "." + chapter + "." + verse;
					String content = StringProc.stripTags(str, module.HtmlFilter, true)
						.replaceAll("^\\d+\\s+", "");
					SearchRes.put(linkOSIS, content);
				}
			}
			bReader.close();
		} catch (IOException e) {
			Log.e(TAG, e);
		}
	}
	
	private BufferedReader getReader(String dir, String path, String defaultEncoding) {
		File file = new File(dir, path);
		if (!file.exists()) {
			return null;
		}

		BufferedReader bReader = FileUtilities.OpenFile(file, defaultEncoding);
		if (bReader == null) {
			return null;
		}
		return bReader;
	}
	
	
	private Module getValidModule(Module module) {
		if (module.getIsClosed()) {
			module = mRepository.loadModuleById((String) module.getDataSourceID());
		}
		return module;
	}
	
}
