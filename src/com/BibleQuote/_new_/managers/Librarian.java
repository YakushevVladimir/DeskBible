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
package com.BibleQuote._new_.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import android.content.Context;

import com.BibleQuote._new_.controllers.CacheController;
import com.BibleQuote._new_.controllers.IBookController;
import com.BibleQuote._new_.controllers.IChapterController;
import com.BibleQuote._new_.controllers.IModuleController;
import com.BibleQuote._new_.controllers.LibraryController;
import com.BibleQuote._new_.controllers.LibraryController.LibrarySource;
import com.BibleQuote._new_.dal.CacheContext;
import com.BibleQuote._new_.listeners.ChangeBooksEvent;
import com.BibleQuote._new_.listeners.ChangeModulesEvent;
import com.BibleQuote._new_.listeners.IChangeBooksListener;
import com.BibleQuote._new_.listeners.IChangeModulesListener;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote._new_.models.Verse;
import com.BibleQuote._new_.utils.DataConstants;
import com.BibleQuote.entity.BibleBooksID;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.StringProc;

public class Librarian implements IChangeModulesListener, IChangeBooksListener  {
	
	private final String TAG = "Librarian";
	
	private TreeMap<String, Module> modules  = new TreeMap<String, Module>();
	private LinkedHashMap<String, String> searchResults = new LinkedHashMap<String, String>();
	private ArrayList<Verse> verses = new ArrayList<Verse>();
	
	private Module currModule; 
	private Book currBook; 
	private Integer currChapter = 1;
	private CacheController cacheCtrl;
	
	private IModuleController moduleCtrl;
	private IBookController bookCtrl;
	private IChapterController chapterCtrl;
	private LibraryController libCtrl; 
	
	private static final Byte delimeter1 = (byte) 0xFE;
	private static final Byte delimeter2 = (byte) 0xFF;

	public EventManager eventManager = new EventManager();
	
	
	/**
	 * Производит заполнение списка доступных модулей с книгами Библии,
	 * апокрифами, книгами
	 */
	public Librarian(Context context) {
		Log.i(TAG, "Инициализация библиотеки модулей");
		
		eventManager.addChangeModulesListener(this);
		eventManager.addChangeBooksListener(this);
		
		cacheCtrl = new CacheController(new CacheContext(context.getCacheDir(), DataConstants.LIBRARY_CACHE));
		
		libCtrl = LibraryController.create(LibrarySource.FileSystem, eventManager, context);
		moduleCtrl = libCtrl.getModuleCtrl();
		bookCtrl = libCtrl.getBookCtrl();
		chapterCtrl = libCtrl.getChapterCtrl();
		
		if (cacheCtrl.isCacheExist(DataConstants.LIBRARY_CACHE)) {
			modules = cacheCtrl.loadModules(DataConstants.LIBRARY_CACHE);
			//moduleCtrl.loadModulesAsync();
		} else {
			modules = moduleCtrl.loadModules();
			cacheCtrl.saveModulesAsync(modules, DataConstants.LIBRARY_CACHE);
		}
		
		if (currModule == null && modules.size() > 0) {
			currModule = modules.get(modules.firstKey());
			ArrayList<Book> books = bookCtrl.getBookList(currModule);
			if (books.size() > 0) {
				currBook = books.get(0);
			}
			currChapter = currModule.ChapterZero ? 0 : 1;
		}
	}

	@Override
	public void onChangeModules(ChangeModulesEvent event) {
		if (event.code == IChangeModulesListener.ChangeCode.ModulesLoaded) {
			modules = event.modules;
			cacheCtrl.saveModulesAsync(modules, DataConstants.LIBRARY_CACHE);
		}
	}
	
	@Override
	public void onChangeBooks(ChangeBooksEvent event) {
		if (event.code == IChangeBooksListener.ChangeCode.BooksLoaded) {
		}		
	}
	
	///////////////////////////////////////////////////////////////////////////
	// NAVIGATION
	
	/**
	 * Возвращает список доступных модулей с Библиями, апокрифами, книгами
	 * @return возвращает ArrayList, содержащий модули с книгами Библии и апокрифами 
	 */
	public ArrayList<ItemList> getModulesList() {
		// Сначала отсортируем список по наименованием модулей
		TreeMap<String, Module> tMap = new TreeMap<String, Module>();
		for (Module currModule : modules.values()) {
			tMap.put(currModule.Name, currModule);
		}
		
		// Теперь создадим результирующий список на основе отсортированных данных
		ArrayList<ItemList> moduleList = new ArrayList<ItemList>();
		for (Module currModule : tMap.values()) {
			moduleList.add(new ItemList(currModule.ShortName, currModule.Name));
		}

		return moduleList;
	}
	
	public ArrayList<ItemList> getModuleBooksList(String moduleID) {
		// Получим модуль по его ID
		currModule = modules.get(moduleID);
		if (currModule == null) {
			return new ArrayList<ItemList>();
		}
		ArrayList<ItemList> booksList = new ArrayList<ItemList>();
		for (Book book : bookCtrl.getBookList(currModule)) {
			booksList.add(new ItemList(book.OSIS_ID, book.Name));
		}
		return booksList;
	}

	public ArrayList<ItemList> getModuleBooksList() {
		if (currModule == null) {
			return new ArrayList<ItemList>();
		}
		return this.getModuleBooksList(currModule.ShortName);
	}

	/**
	 * Возвращает список глав книги
	 */
	public ArrayList<String> getChaptersList(String moduleID, String bookID) {
		// Получим модуль по его ID
		currModule = getModule(moduleID);
		if (currModule == null) {
			return new ArrayList<String>();
		}
		currBook = bookCtrl.getBook(currModule, bookID);
		if (currBook == null) {
			return new ArrayList<String>();
		}
		return currBook.getChapterNumbers(currModule.ChapterZero);
	}

	private Module getModule(String moduleID){
		if (modules.containsKey(moduleID)) {
			return modules.get(moduleID);
		} else {
			return null;
		}
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
		currModule  = modules.get(moduleID);
		currBook    = bookCtrl.getBook(currModule, bookID);
		currChapter = chapter;

		String chapterText = getChapterHTMLView(moduleID, bookID, currChapter);
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
	
	public String getFirstChapter(String moduleID, String bookID) {
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return "-";
		} else {
			return currModule.ChapterZero ? "0" : "1";
		}
	}
	
	public void nextChapter(){
		if (currModule == null || currBook == null) {
			return;
		}
		
		Integer chapterQty = currBook.ChapterQty;
		if (chapterQty > (currChapter + (currModule.ChapterZero ? 1 : 0))) {
			currChapter++;
		} else {
			ArrayList<Book> books = bookCtrl.getBookList(currModule);
			int pos = books.indexOf(currBook);
			if (++pos < books.size()) {
				currBook = books.get(pos);
				currChapter = currModule.ChapterZero ? 0 : 1;
			}
		}
	}

	public void prevChapter(){
		if (currModule == null || currBook == null) {
			return;
		}
		
		if (currChapter != (currModule.ChapterZero ? 0 : 1)) {
			currChapter--;
		} else {
			ArrayList<Book> books = bookCtrl.getBookList(currModule);
			int pos = books.indexOf(currBook);
			if (pos > 0) {
				currBook = books.get(--pos);
				Integer chapterQty = currBook.ChapterQty;
				currChapter = chapterQty - (currModule.ChapterZero ? 1 : 0);
			}
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	// GET CONTENT
	
	public String getChapterHTMLView(String moduleID, String bookID, Integer chapter) {
		Log.i(TAG, "getChapterHTMLView(" + moduleID + ", " + bookID + ", " + chapter  + ")");
		// Получим модуль по его ID
		Module currModule = getModule(moduleID);
		if (currModule == null) {
			return "";
		}
		
		// Получим книгу модуля по её ID
		Book currBook = bookCtrl.getBook(currModule, bookID);
		if (currBook == null) {
			return "";
		}
		
		verses = chapterCtrl.getChapter(currBook, chapter).getVerseList(); 
		StringBuilder chapterHTML = new StringBuilder();
		for (int verse = 1; verse <= verses.size(); verse++) {
			String verseText = verses.get(verse - 1).getText();

			if (currModule.containsStrong) {
				// убираем номера Стронга
				verseText = verseText.replaceAll("\\s(\\d)+", "");
			}
			
			verseText = StringProc.stripTags(verseText, currModule.HtmlFilter, false);
			verseText = verseText.replaceAll("<a\\s+?href=\"verse\\s\\d+?\">(\\d+?)</a>", "<b>$1</b>");
			if (currModule.isBible) {
				verseText = verseText
						.replaceAll("^(<[^/]+?>)*?(\\d+)(</(.)+?>){0,1}?\\s+",
								"$1<b>$2</b>$3 ").replaceAll(
								"null", "");
			}

			chapterHTML.append(
				"<div id=\"verse_" + verse + "\" class=\"verse\">"
				+ verseText.replaceAll("<(/)*div(.*?)>", "<$1p$2>")
				+ "</div>"
				+ "\r\n");
		}

		return chapterHTML.toString();
	}
	
	public String getVerseText(Integer verse) {
		if (verses.size() < --verse) {
			return "";
		};
		return StringProc.stripTags(this.verses.get(verse).getText(), "", true)
			.replaceAll("^\\d+\\s+", "")
			.replaceAll("\\s\\d+", "");
	}
	
	public Boolean isBible() {
		if (currModule == null) {
			return false;
		} else {
			return currModule.isBible;
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	// BOOKMARKS
	
	public void addBookmark(Integer verse){
		String fav = PreferenceHelper.restoreStateString("Favorits");
		fav = this.getCurrentLink() + ":" + verse + delimeter2
			+ this.getCurrentOSISLink() + "." + verse + delimeter1
			+ fav;
		PreferenceHelper.saveStateString("Favorits", fav);
	}
	
	public void delBookmark(String bookmark){
		String fav = PreferenceHelper.restoreStateString("Favorits");
		fav = fav.replaceAll(bookmark + "(.)+?" + delimeter1, "");
		PreferenceHelper.saveStateString("Favorits", fav);
	}
	
	public ArrayList<String> getBookmarks() {
		ArrayList<String> favorits = new ArrayList<String>();
		String fav = PreferenceHelper.restoreStateString("Favorits");
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
		String fav = PreferenceHelper.restoreStateString("Favorits");
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
		String fav = PreferenceHelper.restoreStateString("Favorits");
		if (!fav.equals("")) {
			TreeSet<String> favorits = new TreeSet<String>();
			favorits.addAll(Arrays.asList(fav.split(delimeter1.toString())));
			StringBuilder newFav = new StringBuilder();
			for (String favItem : favorits) {
				newFav.append(favItem + delimeter1);
			}
			PreferenceHelper.saveStateString("Favorits", newFav.toString());
		}
	}

	public void delAllBookmarks() {
		PreferenceHelper.saveStateString("Favorits", "");
	}

	
	///////////////////////////////////////////////////////////////////////////
	// SEARCH
	
	public void setSearchResults(LinkedHashMap<String, String> searchResults) {
		this.searchResults = searchResults;
	}

	public LinkedHashMap<String, String> getSearchResults() {
		return searchResults;
	}
	
	public LinkedHashMap<String, String> search(String query, String fromBook, String toBook){
		if (currModule == null) {
			return new LinkedHashMap<String, String>();
		} else {
			return new LinkedHashMap<String, String>(); //currModule.search(query, fromBook, toBook);
		}
	}

	
//	///////////////////////////////////////////////////////////////////////////
//	// GET LINK OF STRING
//		
	public String getModuleFullName(String moduleID){
		if (currModule == null) {
			return "";
		}
		return currModule.Name;
	}

	public String getBookFullName(String moduleID, String bookID){
		// Получим модуль по его ID
		Module module = getModule(moduleID);
		if (module == null) {
			return "---";
		} else {
			Book book = bookCtrl.getBook(module, bookID);
			if (book == null) {
				return "---";
			}
			return book.Name;
		}
	}

	public String getBookShortName(String moduleID, String bookID){
		// Получим модуль по его ID
		Module module = getModule(moduleID);
		if (module == null) {
			return "---";
		} else {
			Book book = bookCtrl.getBook(module, bookID);
			if (book == null) {
				return "---";
			}
			return book.getShortName();
		}
	}

	public String getCurrentLink(){
		return getCurrentLink(true);
	}
	
	public String getCurrentLink(boolean includeModuleID){
		return (includeModuleID ? currModule.ShortName + ": " : "") 
			+ currBook.getShortName() + " " + currChapter;
	}
	
	public CharSequence getModuleName() {
		if (currModule == null) {
			return "";
		} else {
			return currModule.Name;
		}
//		String moduleName = currModule.getName();
//		if (moduleName.length() > 40) {
//			int strLenght = moduleName.length();
//			moduleName = moduleName.substring(0, 18) + "..." + moduleName.substring(strLenght - 18, strLenght);
//		}
//		return moduleName;
	}
	
	public CharSequence getHumanBookLink() {
		if (currModule == null || currBook == null) {
			return "";
		}
		String bookLink = currBook.getShortName() + " " + currChapter;
		if (bookLink.length() > 10) {
			int strLenght = bookLink.length();
			bookLink = bookLink.substring(0, 4) + "..." + bookLink.substring(strLenght - 4, strLenght);
		}
		return bookLink;
	}
	
	public String getCurrentOSISLink(){
		if (currModule == null || currBook == null) {
			return "";
		}
		return currModule.ShortName + "." + currBook.OSIS_ID + "." + currChapter;
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
		Book currBook = bookCtrl.getBook(currModule, bookID);
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


	///////////////////////////////////////////////////////////////////////////
	// SHARE

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
		
		shareText.append(" (" + getCurrentLink(false) + ":" + verseLink + ")");
		if (currModule != null && currBook != null) {
			shareText.append("- http://b-bq.eu/" 
				+ currBook.OSIS_ID + "/" + currChapter + "_" + verseLink 
				+ "/" + currModule.ShortName); 
		}
		
		return shareText.toString();
	}


}
