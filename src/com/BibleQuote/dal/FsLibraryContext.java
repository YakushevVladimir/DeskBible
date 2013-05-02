/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.dal;

import android.content.Context;
import android.util.Log;
import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.modules.*;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.StringProc;
import com.BibleQuote.utils.modules.LanguageConvertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

	public ArrayList<FsModule> getModuleList(Map<String, Module> moduleSet) {
		ArrayList<FsModule> result = new ArrayList<FsModule>();
		for (Module currModule : moduleSet.values()) {
			result.add((FsModule) currModule);
		}
		return result;
	}

	public ArrayList<FsBook> getBookList(Map<String, Book> bookSet) {
		ArrayList<FsBook> bookList = new ArrayList<FsBook>();
		if (bookSet != null) {
			for (Book currBook : bookSet.values()) {
				bookList.add((FsBook) currBook);
			}
		}
		return bookList;
	}

	public ArrayList<Chapter> getChapterList(Map<String, Chapter> chapterSet) {
		ArrayList<Chapter> chapterList = new ArrayList<Chapter>();
		for (Chapter currChapter : chapterSet.values()) {
			chapterList.add(currChapter);
		}
		return chapterList;
	}

	public BufferedReader getModuleReader(FsModule fsModule) throws FileAccessException {
		return fsModule.isArchive
				? FsUtils.getTextFileReaderFromZipArchive(fsModule.modulePath, fsModule.iniFileName, fsModule.defaultEncoding)
				: FsUtils.getTextFileReader(fsModule.modulePath, fsModule.iniFileName, fsModule.defaultEncoding);
	}


	public BufferedReader getBookReader(FsBook book) throws FileAccessException {
		FsModule fsModule = (FsModule) book.getModule();
		BufferedReader reader = fsModule.isArchive
				? FsUtils.getTextFileReaderFromZipArchive(fsModule.modulePath, book.getDataSourceID(), fsModule.defaultEncoding)
				: FsUtils.getTextFileReader(fsModule.modulePath, book.getDataSourceID(), fsModule.defaultEncoding);
		return reader;
	}

	/**
	 * Выполняет поиск папок с модулями Цитаты на внешнем носителе устройства
	 *
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
			Log.e(TAG, "SearchModules()", e);
			return iniFiles;
		}

		return iniFiles;
	}

	public void fillModule(FsModule module, BufferedReader bReader) throws FileAccessException {
		String str, HTMLFilter = "", key, value;

		int pos;
		try {
			while ((str = bReader.readLine()) != null) {
				pos = str.indexOf("//");
				if (str.toLowerCase().contains("language") && pos >= 0) {
					// Тег языка в старых модулях может быть закомментирован,
					// поэтому раскомментируем его
					str = str.substring(str.toLowerCase().indexOf("language"));
					pos = str.indexOf("//");
				}
				if (pos >= 0) str = str.substring(0, pos);

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
				} else if (key.equals("language")) {
					module.language = LanguageConvertor.getISOLanguage(value);
				} else if (key.equalsIgnoreCase("PathName")) {
					break;
				}
			}

		} catch (IOException e) {
			String message = String.format("fillModule(%1$s)", module.getDataSourceID());
			Log.e(TAG, message, e);
			throw new FileAccessException(message);
		}

		String TagFilter[] = {"p", "b", "i", "em", "strong", "q", "big", "sub", "sup", "h1", "h2", "h3", "h4"};
		ArrayList<String> TagArray = new ArrayList<String>();
		for (String tag : TagFilter) {
			TagArray.add(tag);
		}

		if (!HTMLFilter.equals("")) {
			String[] words = HTMLFilter.replaceAll("\\W", " ").trim().split("\\s+");
			for (String word : words) {
				if (word.equals("") || TagArray.contains(word)) {
					continue;
				}
				TagArray.add(word);
			}
		}

		String separator = "";
		for (String tag : TagArray) {
			module.HtmlFilter += separator + "(" + tag + ")|(/" + tag + ")" + "|(" + tag.toUpperCase() + ")|(/" + tag.toUpperCase() + ")";
			separator = "|";
		}
	}

	public void fillBooks(FsModule module, BufferedReader bReader) throws FileAccessException, BooksDefinitionException, BookDefinitionException {
		String str, key, value;

		ArrayList<String> fullNames = new ArrayList<String>();
		ArrayList<String> pathNames = new ArrayList<String>();
		ArrayList<String> shortNames = new ArrayList<String>();
		ArrayList<Integer> chapterQty = new ArrayList<Integer>();
		int booksCount = 0;

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
				value = delimiterPos >= str.length()
						? ""
						: str.substring(delimiterPos, str.length()).trim();

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
				} else if (key.equals("bookqty")) {
					try {
						booksCount = Integer.valueOf(value);
					} catch (NumberFormatException e) {
					}
				}
			}

		} catch (IOException e) {
			String message = String.format("fillBooks(%1$s)", module.getDataSourceID(), e);
			Log.e(TAG, message, e);
			throw new FileAccessException(message);
		}

		if (booksCount == 0 || pathNames.size() < booksCount || fullNames.size() < booksCount || shortNames.size() < booksCount || chapterQty.size() < booksCount) {
			String message = String.format(
					"Incorrect books definition in module %1$s: BookQty=%2$s, PathNameCount=%3$s, FullNameCount=%4$s, ShortNameCount=%5$s, ChapterQtyCount=%6$s",
					module.getDataSourceID(), booksCount, pathNames.size(), fullNames.size(), shortNames.size(), chapterQty.size());
			throw new BooksDefinitionException(message, module.getDataSourceID(), booksCount, pathNames.size(), fullNames.size(), shortNames.size(), chapterQty.size());
		}

		for (int i = 0; i < booksCount; i++) {
			if (pathNames.get(i).equals("") || fullNames.get(i).equals("") || shortNames.get(i).equals("") || chapterQty.get(i) == 0) {
				// Имя книги, путь к книге и кол-во глав должны быть обязательно указаны
				String message = String.format(
						"Incorrect attributes of book #%1$s in module %2$s: PathName=%3$s, FullName=%4$s, ShortName=%5$s, ChapterQty=%6$s",
						i, module.getDataSourceID(), pathNames.get(i), fullNames.get(i), shortNames.get(i), chapterQty.get(i));
				throw new BookDefinitionException(message, module.getDataSourceID(), i, pathNames.get(i), fullNames.get(i), shortNames.get(i), chapterQty.get(i));
			}
			FsBook book = new FsBook(module, fullNames.get(i), pathNames.get(i),
					(shortNames.size() > i ? shortNames.get(i) : ""),
					chapterQty.get(i));
			module.Books.put(book.getID(), book);
		}
	}

	public String getModuleEncoding(BufferedReader bReader) {
		String encoding = "cp1251";

		if (bReader == null) {
			return encoding;
		}

		HashMap<String, String> charsets = getCharsets();
		String str, key, value;
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
			Log.e(TAG, "getModuleEncoding()", e);
			e.printStackTrace();
			return encoding;
		}

		return encoding;
	}


	public Chapter loadChapter(Book book, Integer chapterNumber, BufferedReader bReader) {

		ArrayList<String> lines = new ArrayList<String>();
		try {
			String str;
			int currentChapter = book.getModule().ChapterZero ? 0 : 1;
			String chapterSign = book.getModule().ChapterSign;
			boolean chapterFind = false;
			while ((str = bReader.readLine()) != null) {
				if (str.toLowerCase().contains(chapterSign)) {
					if (chapterFind) {
						// Тег начала главы может быть не вначале строки.
						// Возьмем то, что есть до теги начала главы и добавим
						// к найденным строкам
						str = str.substring(0, str.toLowerCase().indexOf(chapterSign));
						if (str.trim().length() > 0) {
							lines.add(str);
						}
						break;
					} else if (currentChapter++ == chapterNumber) {
						chapterFind = true;
						// Тег начала главы может быть не вначале строки.
						// Обрежем все, что есть до теги начала главы и добавим
						// к найденным строкам
						str = str.substring(str.toLowerCase().indexOf(chapterSign));
					}
				}
				if (!chapterFind) {
					continue;
				}

				lines.add(str);
			}
		} catch (IOException e) {
			Log.e(TAG, String.format("loadChapter(%1$s, %2$s)", book.getID(), chapterNumber), e);
			return null;
		}

		ArrayList<Verse> verseList = new ArrayList<Verse>();
		String verseSign = book.getModule().VerseSign;
		int i = -1;
		for (String currLine : lines) {
			if (currLine.toLowerCase().contains(verseSign)) {
				i++;
				verseList.add(new Verse(i, currLine));
			} else if (verseList.size() > 0) {
				verseList.set(i, new Verse(i, verseList.get(i).getText() + " " + currLine));
			}
		}

		return new Chapter(book, chapterNumber, verseList);
	}


	public LinkedHashMap<String, String> searchInBook(Module module, String bookID, String searchQuery, BufferedReader bReader) {
		LinkedHashMap<String, String> searchRes = new LinkedHashMap<String, String>();

		// Подготовим регулярное выражение для поиска
		searchQuery = getSearchQuery(searchQuery);
		if (searchQuery.equals("")) {
			return searchRes;
		}

		String str;
		int chapterNumber = module.ChapterZero ? -1 : 0;
		int verseNumber = 0;
		try {
			while ((str = bReader.readLine()) != null) {
				str = str.replaceAll("\\s(\\d)+", "");
				if (str.toLowerCase().contains(module.ChapterSign)) {
					chapterNumber++;
					verseNumber = 0;
				}
				if (str.toLowerCase().contains(module.VerseSign)) {
					verseNumber++;
				}
				if (str.toLowerCase().matches(searchQuery)) {
					BibleReference osisLink = new BibleReference(module.getID(), bookID, chapterNumber, verseNumber);
					String content = StringProc.cleanVerseNumbers(StringProc.stripTags(str));
					searchRes.put(osisLink.getPath(), content);
				}
			}
		} catch (IOException e) {
			Log.e(TAG, String.format("searchInBook(%1$s, %2$s, %3$s)", module.getID(), bookID, searchQuery), e);
			e.printStackTrace();
		}
		return searchRes;
	}

	private String getSearchQuery(String query) {
		String result = "";
		if (query.trim().equals("")) return result;

		String[] words = query.toLowerCase().replaceAll("[^\\s\\w]", "").split("\\s+");
		for (String currWord : words) {
			result += (result.equals("") ? "" : "\\s(.)*?") + currWord;
		}
		return ".*?" + result + ".*?"; // любые символы в начале и конце
	}
}
