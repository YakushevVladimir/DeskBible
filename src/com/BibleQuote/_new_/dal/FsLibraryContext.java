package com.BibleQuote._new_.dal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import android.content.Context;

import com.BibleQuote._new_.controllers.CacheModuleController;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote._new_.utils.FsUtils;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.Log;

public class FsLibraryContext extends LibraryContext {
	private final String TAG = "FsLibraryContext";
	private File libraryDir = null;
	public CacheModuleController<FsModule> cache;

	public FsLibraryContext(File libraryDir, Context context, CacheModuleController<FsModule> cache) {
		super(context);
		this.cache = cache;
		this.libraryDir = libraryDir;
		if (libraryDir != null && !libraryDir.exists()) {
			libraryDir.mkdir();
		}		
	}

		
	public CacheModuleController<FsModule> getCache() {
		return cache;
	}
	
	
	private boolean isLibraryExist() {
		return libraryDir != null && libraryDir.exists();
	}
	
	
	public ArrayList<FsModule> getModuleList(TreeMap<String, Module> moduleSet) {
		ArrayList<FsModule> moduleList = new ArrayList<FsModule>();
		for (Module currModule : moduleSet.values()) {
			moduleList.add((FsModule)currModule);
		}
		return moduleList;
	}
	
	
	public ArrayList<FsBook> getBookList(LinkedHashMap<String, Book> bookSet) {
		ArrayList<FsBook> bookList = new ArrayList<FsBook>();
		for (Book currBook : bookSet.values()) {
			bookList.add((FsBook)currBook);
		}
		return bookList;
	}
	
	
	public ArrayList<Chapter> getChapterList(LinkedHashMap<Integer, Chapter> chapterSet) {
		ArrayList<Chapter> chapterList = new ArrayList<Chapter>();
		for (Chapter currChapter : chapterSet.values()) {
			chapterList.add(currChapter);
		}
		return chapterList;
	}	
	
	
	public BufferedReader getModuleReader(FsModule fsModule) {
		return fsModule.isArchive 
				? FsUtils.getTextFileReaderFromZipArchive(fsModule.modulePath, fsModule.iniFileName, fsModule.defaultEncoding)
				: FsUtils.getTextFileReader(fsModule.modulePath, fsModule.iniFileName, fsModule.defaultEncoding);
	}
	
	
	public BufferedReader getBookReader(FsBook book) {
		FsModule fsModule = (FsModule) book.getModule();
		BufferedReader reader = fsModule.isArchive 
				? FsUtils.getTextFileReaderFromZipArchive(fsModule.modulePath, book.getDataSourceID(), fsModule.defaultEncoding)
				: FsUtils.getTextFileReader(fsModule.modulePath, book.getDataSourceID(), fsModule.defaultEncoding);
		return reader;
	}
	
	
	/**
	 * Выполняет поиск папок с модулями Цитаты на внешнем носителе устройства
	 * @return Возвращает ArrayList со списком ini-файлов модулей
	 */
	public ArrayList<String> SearchModules(FileFilter filter) {

		Log.i(TAG, "SearchModules()");

		ArrayList<String> iniFiles = new ArrayList<String>();

		if (!isLibraryExist()) {
			return iniFiles;
		}

		try {
			// Рекурсивная функция проходит по всем каталогам в поисках ini-файлов Цитаты
			FsUtils.SearchByFilter(libraryDir, iniFiles, filter);
		} catch (Exception e) {
			Log.i(TAG, "Exception in SearchModules(): \r\n" + e.getLocalizedMessage());
			return iniFiles;
		}

		return iniFiles;
	}
	
	
	public void fillModule(FsModule module, BufferedReader bReader) throws CreateModuleErrorException {
		String str, HTMLFilter = "", key, value;

		int pos;
		try {
			while ((str = bReader.readLine()) != null) {
				pos = str.indexOf("//");
				if (pos >= 0)
					str = str.substring(0, pos);

				int delimiterPos = str.indexOf("=");
				if (delimiterPos == -1) {
					continue;
				}

				key = str.substring(0, delimiterPos).trim().toLowerCase();
				delimiterPos++;
				value = delimiterPos >= str.length() ? "" : str.substring(
						delimiterPos, str.length()).trim();

				if (key.equals("biblename")) {
					module.setName(value);
				} else if (key.equals("bibleshortname")) {
					module.ShortName = value.replaceAll("\\.", "");
				} else if (key.equals("chaptersign")) {
					module.ChapterSign = value.toLowerCase();
				} else if (key.equals("chapterzero")) {
					module.ChapterZero = value.toLowerCase().contains("y") ? true : false;
				} else if (key.equals("versesign")) {
					module.VerseSign = value.toLowerCase();
				} else if (key.equals("htmlfilter")) {
					HTMLFilter = value;
				} else if (key.equals("bible")) {
					module.isBible = value.toLowerCase().contains("y") ? true : false;
				} else if (key.equals("strongnumbers")) {
					module.containsStrong = value.toLowerCase().contains("y") ? true : false;
				} 
			}
			
		} catch (IOException e) {
			Log.e(TAG, e);
		}

		String TagFilter[] = { "p", "b", "i", "em", "strong", "q", "big",
				"sub", "sup", "h1", "h2", "h3", "h4" };
		ArrayList<String> TagArray = new ArrayList<String>();
		for (String tag : TagFilter) {
			TagArray.add(tag);
		}

		if (!HTMLFilter.equals("")) {
			String[] words = HTMLFilter.replaceAll("\\W", " ").trim()
					.split("\\s+");
			for (String word : words) {
				if (word.equals("") || TagArray.contains(word)) {
					continue;
				}
				TagArray.add(word);
			}
		}

		String separator = "";
		for (String tag : TagArray) {
			module.HtmlFilter += separator + "(" + tag + ")|(/" + tag + ")" + "|("
					+ tag.toUpperCase() + ")|(/" + tag.toUpperCase() + ")";
			separator = "|";
		}
		
		module.setIsInvalidated(false);
	}	
	
	
	public void fillBooks(FsModule module, BufferedReader bReader) throws CreateModuleErrorException {
		String str, key, value;
		ArrayList<String> fullNames = new ArrayList<String>();
		ArrayList<String> pathNames = new ArrayList<String>();
		ArrayList<String> shortNames = new ArrayList<String>();
		ArrayList<Integer> chapterQty = new ArrayList<Integer>();

		int pos;
		try {
			while ((str = bReader.readLine()) != null) {
				pos = str.indexOf("//");
				if (pos >= 0)
					str = str.substring(0, pos);

				int delimiterPos = str.indexOf("=");
				if (delimiterPos == -1) {
					continue;
				}

				key = str.substring(0, delimiterPos).trim().toLowerCase();
				delimiterPos++;
				value = delimiterPos >= str.length() ? "" : str.substring(
						delimiterPos, str.length()).trim();

				if (key.equals("pathname")) {
					pathNames.add(value);
				} else if (key.equals("fullname")) {
					fullNames.add(value);
				} else if (key.equals("shortname")) {
					shortNames.add(value.replaceAll("\\.", ""));
				} else if (key.equals("chapterqty")) {
					try {
						chapterQty.add(Integer.valueOf(value));
					} catch (NumberFormatException e) {
						chapterQty.add(0);
					}
				}
			}
			
		} catch (IOException e) {
			Log.e(TAG, e);
		}

		for (int i = 0; i < fullNames.size(); i++) {
			if (pathNames.size() < i || chapterQty.size() < i) {
				break;
			} else if (fullNames.get(i).equals("")
					|| pathNames.get(i).equals("") || chapterQty.get(i) == 0) {
				// Имя книги, путь к книге и кол-во глав должны быть обязательно
				// указаны
				continue;
			}
			FsBook book = new FsBook(module, fullNames.get(i), pathNames.get(i), 
					(shortNames.size() > i ? shortNames.get(i) : ""), 
					chapterQty.get(i));
			module.Books.put(book.getID(), book);
		}
		if (module.Books.size() == 0) {
			throw new CreateModuleErrorException();
		}
	}		
	
	
	public String getModuleEncoding(BufferedReader bReader) {
		String encoding = "cp1251";

		HashMap<String, String> charsets = getCharsets();
		String str = "", key, value;
		try {
			while ((str = bReader.readLine()) != null) {
				int pos = str.indexOf("//");
				if (pos >= 0)
					str = str.substring(0, pos);

				int delimiterPos = str.indexOf("=");
				if (delimiterPos == -1) {
					continue;
				}

				key = str.substring(0, delimiterPos).trim().toLowerCase();
				delimiterPos++;
				value = delimiterPos >= str.length() 
						? "" 
						: str.substring(delimiterPos, str.length()).trim();
				if (key.equals("desiredfontcharset")) {
					return charsets.containsKey(value) ? charsets.get(value) : encoding;
				} else if (key.equals("defaultencoding")) {
					return value;
				}
			}
		} catch (IOException e) {
			return encoding;
		}

		return encoding;
	}
}
