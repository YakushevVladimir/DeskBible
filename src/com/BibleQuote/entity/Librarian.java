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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.FileUtilities;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.StringProc;

public class Librarian {
	
	private final String TAG = "Librarian";

	/** 
	 * Содержит список доступных модулей
	 */
	private TreeMap<String, Module> modules  = new TreeMap<String, Module>();
	
	/**
	 * Содержит ссылки по результатм последнего поиска
	 */
	private LinkedHashMap<String, String> searchResults = new LinkedHashMap<String, String>();

	/**
	 * Текущий открытый модуль
	 */
	private String currModuleID = "---"; 
	
	/**
	 * Текущая открытая книга активного модуля
	 */
	private String currBookID = "---"; 
	
	/**
	 * Текущая открытая глава активной книги
	 */
	private Integer currChapter = 1;
	
	ArrayList<String> verses = new ArrayList<String>();
	SharedPreferences Settings;
	Context mContext;
	
	private static final Byte delimeter1 = (byte) 0xFE;
	private static final Byte delimeter2 = (byte) 0xFF;

	/**
	 * Производит заполнение списка доступных модулей с книгами Библии,
	 * апокрифами, книгами
	 */
	public Librarian(Context context) {
		Log.i(TAG, "Инициализация библиотеки");
		ArrayList<String> bqIniFiles = FileUtilities.SearchModules();
		
		Settings = PreferenceManager.getDefaultSharedPreferences(context);

		// Обрабатываем найденные пути с модулями
		for (String iniFile : bqIniFiles) {
			try {
				Module module = new Module(iniFile);
				modules.put(module.getShortName(), module);
				if (currModuleID.equals("---")) {
					currModuleID = module.getShortName();
					currBookID = this.getFirstModuleBookID(currModuleID);
					currChapter = module.containsChapterZero() ? 0 : 1;
				}
			} catch (CreateModuleErrorException e) {
				continue;
			}
		}
	}
	
	/**
	 * Возвращает список доступных модулей с Библиями, апокрифами, книгами
	 * @return возвращает ArrayList, содержащий модули с книгами Библии и апокрифами 
	 */
	public ArrayList<ItemList> getModulesList() {
		// Сначала отсортируем список по наименованием модулей
		TreeMap<String, Module> tMap = new TreeMap<String, Module>();
		for (Module currModule : modules.values()) {
			tMap.put(currModule.getName(), currModule);
		}
		
		// Теперь создадим результирующий список на основе отсортированных данных
		ArrayList<ItemList> moduleList = new ArrayList<ItemList>();
		for (Module currModule : tMap.values()) {
			moduleList.add(new ItemList(currModule.getShortName(), currModule
					.getName()));
		}

		return moduleList;
	}
	
	public ArrayList<ItemList> getModuleBooksList(String moduleID) {
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return new ArrayList<ItemList>();
		}
		return currModule.getBooksList();
	}

	public ArrayList<ItemList> getModuleBooksList() {
		return this.getModuleBooksList(currModuleID);
	}

	public ArrayList<String> getModuleBooks(String moduleID) {
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return new ArrayList<String>();
		}
		return currModule.getBooks();
	}

	public ArrayList<String> getModuleBooks() {
		return this.getModuleBooks(currModuleID);
	}

	/**
	 * Возвращает список глав книги
	 */
	public ArrayList<String> getChaptersList(String moduleID, String bookID) {
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return new ArrayList<String>();
		}
		return currModule.getChapters(bookID);
	}

	public String getCurrentLink(){
		return getCurrentLink(true);
	}
	
	public String getCurrentLink(boolean includeModuleID){
		return (includeModuleID ? currModuleID + ": " : "") 
			+ this.getBookShortName(currModuleID, currBookID) + " " + currChapter;
	}
	
	public String getCurrentOSISLink(){
		return currModuleID + "." + currBookID + "." + currChapter;
	}
	
	private Module getModule(String moduleID){
		if (modules.containsKey(moduleID)) {
			return modules.get(moduleID);
		} else {
			return null;
		}
	}
	
	public String getChapterHTMLView(String moduleID, String bookID, Integer chapter) {
		Log.i(TAG, "getChapterHTMLView(" + moduleID + ", " + bookID + ", " + chapter  + ")");
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return "";
		}
		
		// Получим книгу модуля по её ID
		Book currBook = currModule.getBook(bookID);
		if (currBook == null) {
			return "";
		}
		
		verses = currModule.getChapterVerses(currBook, chapter);
		StringBuilder chapterHTML = new StringBuilder();
		for (int verse = 1; verse <= verses.size(); verse++) {
			String verseText = verses.get(verse - 1);

			if (currModule.isContainsStrong()) {
				// убираем номера Стронга
				verseText = verseText.replaceAll("\\s(\\d)+", "");
			}
			
			verseText = StringProc.stripTags(verseText, currModule.getHtmlFilter(), false);
			verseText = verseText.replaceAll("<a\\s+?href=\"verse\\s\\d+?\">(\\d+?)</a>", "<b>$1</b>");
			if (currModule.isBible()) {
				verseText = verseText
						.replaceAll("^(<[^/]+?>)*?(\\d+)(</(.)+?>){0,1}?\\s+",
								"$1<b>$2</b>$3 ").replaceAll(
								"null", "");
			}

			chapterHTML.append(
				"<div id=\"verse_" + verse + "\" class=\"verse\">"
				+ verseText.replaceAll("<(/)*div(.*?)>", "<$1p$2>")
				+ "</div>"
				+ (currModule.isBible() && splitVerse() ? "<br/>" : "")
				+ "\r\n");
		}

		return chapterHTML.toString();
	}
	
	private Boolean splitVerse() {
		return Settings.getBoolean("SplitVerse", false);
	}

	public String OpenLink(String moduleID, String bookID, String chapter){
		
		Integer chapterInt = 1;
		try {
			chapterInt = Integer.parseInt(chapter);
		} catch (NumberFormatException  e) {
			return "";
		}
		
		return OpenLink(moduleID, bookID, chapterInt);
	}
	
	public String OpenLink(String moduleID, String bookID, Integer chapter){
		currModuleID = moduleID;
		currBookID = bookID;
		currChapter = chapter;

		String chapterText = getChapterHTMLView(currModuleID, currBookID, currChapter);
		return chapterText;
	}
	
	public String OpenLink(String linkOSIS){
		String[] linkParam = linkOSIS.split("\\.");
		if (linkParam.length == 3 || linkParam.length == 4) {
			return OpenLink(linkParam[0], linkParam[1], linkParam[2]);
		} else {
			return "";
		}
	}
	
	public String getFirstModuleBookID(String moduleID) {
		String bookID = "";
		Module currModule = getModule(moduleID);
		if (currModule != null) {
			ArrayList<ItemList> books = currModule.getBooksList();
			if (books.size() > 0) {
				bookID = books.get(0).get("ID");
			}
		}
		return bookID;
	}

	public String getFirstChapter(String moduleID, String bookID) {
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return "-";
		} else {
			return currModule.containsChapterZero() ? "0" : "1";
		}
	}
	
	public String getModuleFullName(String moduleID){
		Module currModule = getModule(currModuleID);
		if (currModule == null) {
			return "";
		}
		return currModule.getName();
	}

	public String getBookFullName(String moduleID, String bookID){
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return "---";
		} else {
			Book currBook = currModule.getBook(bookID);
			if (currBook == null) {
				return "---";
			}
			return currBook.getName();
		}
	}

	public String getBookShortName(String moduleID, String bookID){
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return "---";
		} else {
			Book currBook = currModule.getBook(bookID);
			if (currBook == null) {
				return "---";
			}
			return currBook.getShortName();
		}
	}

	public Boolean isBible() {
		Module currModule = getModule(currModuleID);
		if (currModule == null) {
			return false;
		} else {
			return currModule.isBible();
		}
	}
	
	public void nextChapter(){
		Module currModule = getModule(currModuleID);
		if (currModule == null) {
			return;
		}
		
		Book currBook = currModule.getBook(currBookID);
		if (currBook == null) {
			return;
		}
		
		Integer chapterQty = currBook.getChapterQty();
		if (chapterQty > (currChapter + (currModule.containsChapterZero() ? 1 : 0))) {
			currChapter++;
		} else {
			ArrayList<String> books = currModule.getBooksIDs();
			int pos = books.indexOf(currBookID);
			if (++pos < books.size()) {
				currBookID = books.get(pos);
				currChapter = currModule.containsChapterZero() ? 0 : 1;
			}
		}
	}

	public void prevChapter(){
		Module currModule = getModule(currModuleID);
		if (currModule == null) {
			return;
		}
		
		if (currChapter != (currModule.containsChapterZero() ? 0 : 1)) {
			currChapter--;
		} else {
			ArrayList<String> books = currModule.getBooksIDs();
			int pos = books.indexOf(currBookID);
			if (pos > 0) {
				currBookID = books.get(--pos);
				Book currBook = currModule.getBook(currBookID);
				if (currBook == null) {
					return;
				}
				Integer chapterQty = currBook.getChapterQty();
				currChapter = chapterQty - (currModule.containsChapterZero() ? 1 : 0);
			}
		}
	}
	
	public void addBookmark(Integer verse){
		String fav = Settings.getString("Favorits", "");
		fav = this.getCurrentLink() + ":" + verse + delimeter2
			+ this.getCurrentOSISLink() + "." + verse + delimeter1
			+ fav;
		Settings.edit().putString("Favorits", fav).commit();
	}
	
	public void delBookmark(String bookmark){
		String fav = Settings.getString("Favorits", "");
		fav = fav.replaceAll(bookmark + "(.)+?" + delimeter1, "");
		Settings.edit().putString("Favorits", fav).commit();
	}
	
	public ArrayList<String> getBookmarks() {
		ArrayList<String> favorits = new ArrayList<String>();
		String fav = Settings.getString("Favorits", "");
		if (!fav.equals("")) {
			favorits.addAll(Arrays.asList(fav.split(delimeter1.toString())));
		}
		
		ArrayList<String> ret = new ArrayList<String>();
		for (String favItem : favorits) {
			ret.add(favItem.split(delimeter2.toString())[0]);
		}
		return ret;
	}
	
	public String getBookmark(String humanLink) {
		ArrayList<String> favorits = new ArrayList<String>();
		String fav = Settings.getString("Favorits", "");
		if (!fav.equals("")) {
			favorits.addAll(Arrays.asList(fav.split(delimeter1.toString())));
			for (String favItem : favorits) {
				if (favItem.contains(humanLink)) {
					return favItem.split(delimeter2.toString())[1];
				}
			}
		}
		
		return "";
	}

	public void sortBookmarks() {
		String fav = Settings.getString("Favorits", "");
		if (!fav.equals("")) {
			TreeSet<String> favorits = new TreeSet<String>();
			favorits.addAll(Arrays.asList(fav.split(delimeter1.toString())));
			StringBuilder newFav = new StringBuilder();
			for (String favItem : favorits) {
				newFav.append(favItem + delimeter1);
			}
			Settings.edit().putString("Favorits", newFav.toString()).commit();
		}
	}

	public void delAllBookmarks() {
		Settings.edit().putString("Favorits", "").commit();
	}

	public void setSearchResults(LinkedHashMap<String, String> searchResults) {
		this.searchResults = searchResults;
	}

	public LinkedHashMap<String, String> getSearchResults() {
		return searchResults;
	}
	
	public LinkedHashMap<String, String> search(String query, String fromBook, String toBook){
		Module currModule = getModule(currModuleID);
		if (currModule == null) {
			return new LinkedHashMap<String, String>();
		} else {
			return currModule.search(query, fromBook, toBook);
		}
	}

	public CharSequence getModuleName() {
		Module currModule = getModule(currModuleID);
		if (currModule == null) {
			return "";
		}
		String moduleName = currModule.getName();
		if (moduleName.length() > 40) {
			int strLenght = moduleName.length();
			moduleName = moduleName.substring(0, 18) + "..." + moduleName.substring(strLenght - 18, strLenght);
		}
		return moduleName;
	}
	
	public CharSequence getHumanBookLink(String moduleID, String bookID, int chapter) {
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return "";
		}
		Book currBook = currModule.getBook(bookID);
		if (currBook == null) {
			return "";
		}
		String bookLink = currBook.getShortName() + " " + chapter;
		if (bookLink.length() > 10) {
			int strLenght = bookLink.length();
			bookLink = bookLink.substring(0, 4) + "..." + bookLink.substring(strLenght - 4, strLenght);
		}
		return bookLink;
	}
	
	public CharSequence getHumanBookLink() {
		return this.getHumanBookLink(currModuleID, currBookID, currChapter);
	}
	
	public String getOSIStoHuman(String linkOSIS) {
		String[] param = linkOSIS.split("\\.");
		if (param.length < 3) {
			return "";
		}
		
		String moduleID = param[0];
		String bookID = param[1];
		String chapter = param[2];
		
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return "";
		}
		Book currBook = currModule.getBook(bookID);
		if (currBook == null) {
			return "";
		}
		String humanLink = moduleID + ": " + currBook.getShortName() + " " + chapter;
		if (param.length > 3) {
			humanLink += ":" + param[3];
		}
		
		return humanLink;
	}
	public String getHumanToOSIS(String humanLink) {
		String linkOSIS = "";
		
		// Получим имя модуля
		int position = humanLink.indexOf(":");
		if (position == -1) {
			return "";
		}
		linkOSIS = humanLink.substring(0, position).trim();
		humanLink = humanLink.substring(position + 1).trim();
		if (humanLink.length() == 0) {
			return "";
		}
		
		// Получим имя книги
		position = humanLink.indexOf(" ");
		if (position == -1) {
			return "";
		}
		linkOSIS += "." + BibleBooksID.getID(humanLink.substring(0, position).trim());
		humanLink = humanLink.substring(position).trim();
		if (humanLink.length() == 0) {
			return linkOSIS + ".1";
		}
		
		// Получим номер главы
		position = humanLink.indexOf(":");
		if (position == -1) {
			return "";
		}
		linkOSIS += "." + humanLink.substring(0, position).trim().replaceAll("\\D", "");
		humanLink = humanLink.substring(position).trim().replaceAll("\\D", "");
		if (humanLink.length() == 0) {
			return linkOSIS;
		} else {
			// Оставшийся кусок - номер стиха
			return linkOSIS + "." + humanLink;
		}
	}

	public String getVerseText(Integer verse) {
		if (verses.size() < --verse) {
			return "";
		};
		return StringProc.stripTags(this.verses.get(verse), "", true)
			.replaceAll("^\\d+\\s+", "")
			.replaceAll("\\s\\d+", "");
	}

	public String getShareText(TreeSet<Integer> selectVerses) {
		StringBuilder verseLink = new StringBuilder();
		StringBuilder shareText = new StringBuilder();
		Integer fromVerse = 0;
		Integer toVerse = 0;
		
		for (Integer verse : selectVerses) {
			if (fromVerse == 0) {
				fromVerse = verse;
			} else if ((toVerse + 1) != verse) {
				if (verseLink.length() != 0) {
					verseLink.append(",");
				}
				if (fromVerse == toVerse) {
					verseLink.append(fromVerse);
				} else {
					verseLink.append(fromVerse + "-" + toVerse);
				}
				fromVerse = verse;
				
				shareText.append(" ... ");
			}
			toVerse = verse;
			
			shareText.append(getVerseText(verse));
		}
		if (verseLink.length() != 0) {
			verseLink.append(",");
		}
		if (fromVerse == toVerse) {
			verseLink.append(fromVerse);
		} else {
			verseLink.append(fromVerse + "-" + toVerse);
		}
		
		shareText.append(" (" + getCurrentLink(false) + ":" + verseLink 
				+ ") - http://b-bq.eu/" 
				+ currBookID + "/" + currChapter + "_" + verseLink + "/" + currModuleID);
		return shareText.toString();
	}
}
