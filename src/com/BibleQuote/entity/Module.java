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
package com.BibleQuote.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.FileUtilities;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.StringProc;

/**
 * @author Владимир
 * 
 */
public class Module {

	private final String TAG = "Module";
	
	private String modulePath = "";
	//private String filesEncoding = "cp1251";

	private String Name = "";
	private String ShortName = "";
	private String ChapterSign = "";
	private String VerseSign = "";
	// private String Copyright = "";
	private String XFilter = "";
	// private String Categories = "";
	private boolean ChapterZero = false;
	private boolean containsStrong = false;
	private boolean isBible = false;
	// private boolean containsOT = false;
	// private boolean containsNT = false;
	// private boolean containsAP = false;

	private LinkedHashMap<String, Book> Books = new LinkedHashMap<String, Book>();
	private LinkedHashMap<String, String> SearchRes = new LinkedHashMap<String, String>();

	public Module(String pathINIFile) throws CreateModuleErrorException {
		Log.i(TAG, "Module(" + pathINIFile + ")");

		modulePath = pathINIFile.substring(0, pathINIFile.lastIndexOf("/"));

		File iniFile = new File(pathINIFile);
		if (!iniFile.exists()) {
			return;
		}

		String filesEncoding = FileUtilities.getModuleEncoding(iniFile);

		BufferedReader F = FileUtilities.OpenFile(iniFile, filesEncoding);
		if (F == null) {
			return;
		}

		String str, HTMLFilter = "", key, value;
		ArrayList<String> fullNames = new ArrayList<String>();
		ArrayList<String> pathNames = new ArrayList<String>();
		ArrayList<String> shortNames = new ArrayList<String>();
		ArrayList<Integer> chapterQty = new ArrayList<Integer>();

		int pos;
		try {
			while ((str = F.readLine()) != null) {
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
					Name = value;
				} else if (key.equals("bibleshortname")) {
					ShortName = value.replaceAll("\\.", "");
				} else if (key.equals("chaptersign")) {
					ChapterSign = value.toLowerCase();
				} else if (key.equals("chapterzero")) {
					ChapterZero = value.toLowerCase().contains("y") ? true
							: false;
				} else if (key.equals("versesign")) {
					VerseSign = value.toLowerCase();
					// } else if (key.equals("copyright")) {
					// Copyright = value;
				} else if (key.equals("htmlfilter")) {
					HTMLFilter = value;
					// } else if (key.equals("categories")) {
					// Categories = value;
				} else if (key.equals("bible")) {
					isBible = value.toLowerCase().contains("y") ? true : false;
					// } else if (key.equals("oldtestament")) {
					// containsOT = value.toLowerCase().contains("y") ? true :
					// false;
					// } else if (key.equals("tewtestament")) {
					// containsNT = value.toLowerCase().contains("y") ? true :
					// false;
					// } else if (key.equals("apocrypha")) {
					// containsAP = value.toLowerCase().contains("y") ? true :
					// false;
				} else if (key.equals("strongnumbers")) {
					this.containsStrong = value.toLowerCase().contains("y") ? true
							: false;
				} else if (key.equals("pathname")) {
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
			e.printStackTrace();
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
			Book book = new Book(fullNames.get(i), pathNames.get(i), (shortNames
							.size() > i ? shortNames.get(i) : ""), chapterQty
							.get(i));
			book.setEncoding(filesEncoding);
			Books.put(book.getBookID(), book);
		}
		if (Books.size() == 0) {
			throw new CreateModuleErrorException();
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
			XFilter += separator + "(" + tag + ")|(/" + tag + ")" + "|("
					+ tag.toUpperCase() + ")|(/" + tag.toUpperCase() + ")";
			separator = "|";
		}
	}

	public ArrayList<String> getBooks() {
		ArrayList<String> bookNames = new ArrayList<String>();
		for (Book currBook : Books.values()) {
			bookNames.add(currBook.getName());
		}
		return bookNames;
	}

	public ArrayList<String> getBooksIDs() {
		ArrayList<String> bookIDs = new ArrayList<String>();
		for (String id : Books.keySet()) {
			bookIDs.add(id);
		}
		return bookIDs;
	}

	public ArrayList<ItemList> getBooksList() {
		ArrayList<ItemList> booksList = new ArrayList<ItemList>();
		for (Book currBook : Books.values()) {
			booksList.add(new ItemList(currBook.getBookID(), currBook
					.getName()));
		}
		return booksList;
	}

	public String getShortName() {
		return ShortName;
	}

	public String getName() {
		return Name;
	}

	public String getBookName(String bookID) {
		if (bookID == null) {
			return "";
		}
		Book currBook = Books.get(bookID);
		return currBook.getName();
	}

	public Book getBook(String bookID) {
		if (bookID == null || !Books.containsKey(bookID)) {
			return null;
		}
		return Books.get(bookID);
	}

	/**
	 * Возвращает список глав книги
	 */
	public ArrayList<String> getChapters(String bookID) {
		ArrayList<String> ret = new ArrayList<String>();
		Book currBook = this.getBook(bookID);
		if (currBook != null) {
			for (int i = 0; i < currBook.getChapterQty(); i++) {
				ret.add(String.valueOf(i + (this.ChapterZero ? 0 : 1)));
			}
		}
		return ret;
	}

	public ArrayList<String> getChapterVerses(Book book, Integer chapterToView) {
		ArrayList<String> verses = new ArrayList<String>();

		String chapterFilePath = modulePath + "/" + book.getPath();
		BufferedReader bReader = FileUtilities.OpenFile(chapterFilePath, book.getEncoding());
		if (bReader == null) {
			return verses;
		}

		ArrayList<String> lines = new ArrayList<String>();
		try {
			String str;
			int currentChapter = this.ChapterZero ? 0 : 1;
			boolean chapterFind = false;
			while ((str = bReader.readLine()) != null) {
				if (str.matches("<meta http-equiv=\"Content-Type\" content=\"text/html; Charset=.+?\">")) {
					String encodingCharset = str
							.replaceAll(
									"<meta http-equiv=\"Content-Type\" content=\"text/html; Charset=(.+?)\">",
									"$1");
					book.setEncoding(encodingCharset);
				}
				if (str.toLowerCase().contains(ChapterSign)) {
					if (chapterFind) {
						// Тег начала главы может быть не вначале строки.
						// Возьмем то, что есть до теги начала главы и добавим
						// к найденным строкам
						str = str.substring(0, str.toLowerCase().indexOf(ChapterSign));
						if (str.trim().length() > 0) {
							lines.add(str);
						}
						break;
					} else if (currentChapter++ == chapterToView) {
						chapterFind = true;
						// Тег начала главы может быть не вначале строки.
						// Обрежем все, что есть до теги начала главы и добавим
						// к найденным строкам
						str = str.substring(str.toLowerCase().indexOf(ChapterSign));
					}
				}
				if (!chapterFind){
					continue;
				}
				
				lines.add(str);
			}
		} catch (IOException e) {
			return verses;
		}

		int i = -1;
		for (String currLine : lines) {
			if (currLine.toLowerCase().contains(this.VerseSign)) {
				verses.add(currLine);
				i++;
			} else if (verses.size() > 0) {
				verses.set(i, verses.get(i) + " " + currLine);
			}
		}

		return verses;
	}

	private void searchInBook(String bookID, String regQuery) {
		Book book = Books.get(bookID);
		String chapterFilePath = modulePath + "/" + book.getPath();
		BufferedReader bReader = FileUtilities.OpenFile(chapterFilePath, book.getEncoding());
		if (bReader == null) {
			return;
		}

		String str;
		int chapter = this.containsChapterZero() ? -1 : 0;
		int verse = 0;
		try {
			while ((str = bReader.readLine()) != null) {
				str = str.replaceAll("\\s(\\d)+", "");
				if (str.toLowerCase().contains(ChapterSign)) {
					chapter++;
					verse = 0;
				}
				if (str.toLowerCase().contains(VerseSign))
					verse++;

				if (str.toLowerCase().matches(regQuery)) {
					String linkOSIS = this.ShortName + "." + bookID + "." + chapter + "." + verse;
					String content = StringProc.stripTags(str, this.getHtmlFilter(), true)
						.replaceAll("^\\d+\\s+", "");
					SearchRes.put(linkOSIS, content);
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e);
		}

		try {
			bReader.close();
		} catch (IOException e) {
			Log.e(TAG, e);
		}
	}

	public LinkedHashMap<String, String> search(String query, String fromBookID, String toBookID) {
		LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();

		if (query.trim().equals("")) {
			// Передана пустая строка
			return ret;
		}

		// Подготовим регулярное выражение для поиска
		String regQuery = "";
		String[] words = query.toLowerCase().split("\\s+");
		for (String currWord : words) {
			regQuery += (regQuery.equals("") ? "" : "\\s(.)*?") + currWord;
		}
		regQuery = ".*?" + regQuery + ".*?"; // любые символы в начале и конце

		SearchRes.clear();
		
		boolean startSearch = false;
		for (String bookID : Books.keySet()) {
			if (!startSearch) {
				startSearch = bookID.equals(fromBookID);
				if (!startSearch) {
					continue;
				}
			} 
			searchInBook(bookID, regQuery);
			if (bookID.equals(toBookID)) {
				break;
			}
		}
		return SearchRes;
	}

	public String loadSearchRes(int pos) {
		return SearchRes.get(pos);
	}

	public String toString() {
		return this.Name;
	}

	public boolean isBible() {
		return this.isBible;
	}

	/**
	 * @return Возвращает true, если модуль содержит номера Стронга, иначе false
	 */
	public boolean isContainsStrong() {
		return containsStrong;
	}

	public String getHtmlFilter() {
		return XFilter;
	}
	
	public Boolean containsChapterZero(){
		return this.ChapterZero;
	}
}
