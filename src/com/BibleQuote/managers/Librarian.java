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
package com.BibleQuote.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;

import com.BibleQuote.controllers.IBookController;
import com.BibleQuote.controllers.IChapterController;
import com.BibleQuote.controllers.IModuleController;
import com.BibleQuote.controllers.LibraryController;
import com.BibleQuote.controllers.LibraryController.LibrarySource;
import com.BibleQuote.entity.BibleBooksID;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.listeners.ChangeBooksEvent;
import com.BibleQuote.listeners.IChangeBooksListener;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;
import com.BibleQuote.models.Verse;
import com.BibleQuote.utils.OSISLink;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.StringProc;

public class Librarian implements IChangeBooksListener  {
	
	private final String TAG = "Librarian";
	
	private LinkedHashMap<String, String> searchResults = new LinkedHashMap<String, String>();
	
	private Module currModule; 
	private Book currBook; 
	private Chapter currChapter;
	private Integer currChapterNumber = -1;
	private Integer currVerseNumber = 1;
	
	private IModuleController moduleCtrl;
	private IBookController bookCtrl;
	private IChapterController chapterCtrl;
	private LibraryController libCtrl; 
	
	private static final Byte delimeter1 = (byte) 0xFE;
	private static final Byte delimeter2 = (byte) 0xFF;

	public EventManager eventManager = new EventManager();
	
	/**
	 * Инициализация контроллеров библиотеки, модулей, книг и глав.
	 * Подписка на событие ChangeBooksEvent 
	 */
	public Librarian(Context context) {
		Log.i(TAG, "Librarian()");
		
		eventManager.addChangeBooksListener(this);
		
		libCtrl = LibraryController.create(LibrarySource.FileSystem, eventManager, context);
		moduleCtrl = libCtrl.getModuleCtrl();
		bookCtrl = libCtrl.getBookCtrl();
		chapterCtrl = libCtrl.getChapterCtrl();
	}

	/**
	 * @return Возвращает первый closed-модуль из коллекции модулей
	 */
	public Module getClosedModule() {
		return moduleCtrl.getClosedModule();
	}
	
    /**
     * @return Возвращает TreeMap коллекцию модулей с ключом по Module.ShortName
     */
	public TreeMap<String, Module> getModules() {
		return moduleCtrl.getModules();
	}
	
	/**
	 * Загружает из хранилища список модулей без загрузки их данных. Для каждого из модулей
	 * установлен флаг isClosed = true.
	 * @return Возвращает TreeMap, где в качестве ключа путь к модулю, а в качестве значения 
	 * closed-модуль
	 */
	public TreeMap<String, Module> loadFileModules() {
		return moduleCtrl.loadFileModules();
	}
	
	public void onChangeBooks(ChangeBooksEvent event) {
		if (event.code == IChangeBooksListener.ChangeCode.BooksLoaded) {
		}		
	}
	
	/**
	 * Возвращает коллекцию Book для указанного модуля. Данные о книгах в первую
	 * очередь берутся из контекста библиотеки. Если там для выбранного модуля 
	 * список книг отсутсвует, то производится загрузка коллекции Book из хранилища
	 * @param module модуль для которого необходимо получить коллекцию Book
	 * @return коллекцию Book для указанного модуля
	 * @throws OpenModuleException
	 * @throws BooksDefinitionException
	 * @throws BookDefinitionException
	 */
	public ArrayList<Book> getBookList(Module module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		return bookCtrl.getBookList(module);
	}
	
	/**
	 * Инициализирует полную загрузку модулей. Сначала проверяется наличие
	 * модулей в коллекции. Если коллекция пуста, то производится попытка
	 * загрузки коллекции модулей из кэш. Иначе производится загрузка модулей
	 * из файлового хранилища. Для всех closed-модулей производится
	 * полная загрузка данных. Производится запись загруженных модулей в кэш.
	 * @param incorrectModuleTemplate - строковый шаблон сообщения об ошибках. Должен
	 * содержать место для двух аргументов:
	 * <ul>
	 * <li>ShortName модуля
	 * <li>Пути к данным модуля
	 * </ul>
	 * @return строку, содержащую список ошибок возникших при загрузке модулей
	 */
	public String loadModules(String incorrectModuleTemplate) {
		StringBuilder errorList = new StringBuilder();
		this.getModules();
		Module module = this.getClosedModule();
		while (module != null) {
			try {
				this.getModuleByID(module.getID(), module.getDataSourceID());
			} catch (OpenModuleException e) {
				errorList
					.append( String.format(incorrectModuleTemplate, e.getModuleId(), e.getModuleDatasourceId() ))
					.append("\n\n");
			}
			module = this.getClosedModule();
		}
		return errorList.toString();
	}
	

	public Module getModuleByID(String moduleID, String moduleDatasourceID) throws OpenModuleException {
		return moduleCtrl.getModuleByID(moduleID, moduleDatasourceID);
	}
	
	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, OpenModuleException {
		return bookCtrl.getBookByID(module, bookID);
	}
	
	public Chapter getChapterByNumber(Book book, Integer chapterNumber) throws BookNotFoundException {
		return chapterCtrl.getChapter(book, chapterNumber);
	}
	
	/**
	 * Возвращает полностью загруженный модуль. Ищет модуль в коллекции
	 * модулей. Если он отсутствует в коллекции производит его загрузку
	 * из хранилища. Для closed-модуля инициируется полная загрузка данных 
	 * модуля и обновления кэш.<br/>
	 * Очищаются ссылки в currBook, currChapter, currChapterNumber и CurrVerseNumber
	 * 
	 * @param moduleID ShortName модуля
	 * @param moduleDatasourceID путь к данным модуля в хранилище
	 * @return Возвращает полностью загруженный модуль
	 * @throws OpenModuleException произошла ошибки при попытке загрузки closed-модуля
	 * из хранилища
	 */
	public Module openModule(String moduleID, String moduleDatasourceID) throws OpenModuleException {
		currModule = getModuleByID(moduleID, moduleDatasourceID);
		currBook = null;
		currChapter = null;
		currChapterNumber = -1;
		currVerseNumber = 1;
		return currModule;
	}
	
	public Book openBook(Module module, String bookID) throws BookNotFoundException, OpenModuleException {
		currBook = getBookByID(module, bookID);
		currChapter = null;
		currChapterNumber = -1;
		currVerseNumber = 1;		
		return currBook;
	}
	
	public Chapter openChapter(Book book, Integer chapterNumber, Integer verseNumber) throws BookNotFoundException {
		currChapter = getChapterByNumber(book, chapterNumber);
		currChapterNumber = chapterNumber;
		currVerseNumber = verseNumber != null ? verseNumber : 1;
		return currChapter;
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
		for (Module currModule : moduleCtrl.getModules().values()) {
			tMap.put(currModule.getName(), currModule);
		}
		
		// Теперь создадим результирующий список на основе отсортированных данных
		ArrayList<ItemList> moduleList = new ArrayList<ItemList>();
		for (Module currModule : tMap.values()) {
			moduleList.add(new ItemList(currModule.getID(), currModule.getName(), (String)currModule.getDataSourceID()));
		}

		return moduleList;
	}
	
	
	public ArrayList<ItemList> getModuleBooksList(String moduleID) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		// Получим модуль по его ID
		currModule = moduleCtrl.getModuleByID(moduleID);
		ArrayList<ItemList> booksList = new ArrayList<ItemList>();
		for (Book book : bookCtrl.getBookList(currModule)) {
			booksList.add(new ItemList(book.getID(), book.Name, (String)book.getDataSourceID()));
		}
		return booksList;
	}

	public ArrayList<ItemList> getCurrentModuleBooksList() throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		if (currModule == null) {
			return new ArrayList<ItemList>();
		}
		return this.getModuleBooksList(currModule.getID());
	}

	/**
	 * Возвращает список глав книги
	 * @throws OpenModuleException 
	 * @throws BookNotFoundException 
	 * @throws LoadBooksException 
	 */
	public ArrayList<String> getChaptersList(String moduleID, String bookID) 
			throws BookNotFoundException, OpenModuleException {
		// Получим модуль по его ID
		currModule = getModule(moduleID);
		currBook = bookCtrl.getBookByID(currModule, bookID);
		return currBook.getChapterNumbers(currModule.ChapterZero);
	}

	private Module getModule(String moduleID) throws OpenModuleException{
		return moduleCtrl.getModuleByID(moduleID);
	}
	

	public void nextChapter() throws OpenModuleException{
		if (currModule == null || currBook == null) {
			return;
		}
		
		Integer chapterQty = currBook.ChapterQty;
		if (chapterQty > (currChapterNumber + (currModule.ChapterZero ? 1 : 0))) {
			currChapterNumber++;
			currVerseNumber = 1;
		} else {
			try {
				ArrayList<Book> books = bookCtrl.getBookList(currModule);
				int pos = books.indexOf(currBook);
				if (++pos < books.size()) {
					currBook = books.get(pos);
					currChapterNumber = currBook.getFirstChapterNumber();
					currVerseNumber = 1;
				}
			} catch (BooksDefinitionException e) {
				Log.e(TAG, e.getMessage());
			} catch (BookDefinitionException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	public void prevChapter() throws OpenModuleException{
		if (currModule == null || currBook == null) {
			return;
		}
		
		if (currChapterNumber != currBook.getFirstChapterNumber()) {
			currChapterNumber--;
			currVerseNumber = 1;
		} else {
			try {			
				ArrayList<Book> books = bookCtrl.getBookList(currModule);
				int pos = books.indexOf(currBook);
				if (pos > 0) {
					currBook = books.get(--pos);
					Integer chapterQty = currBook.ChapterQty;
					currChapterNumber = chapterQty - (currModule.ChapterZero ? 1 : 0);
					currVerseNumber = 1;
				}
			} catch (BooksDefinitionException e) {
				Log.e(TAG, e.getMessage());
			} catch (BookDefinitionException e) {
				Log.e(TAG, e.getMessage());
			}			
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	// GET CONTENT
	
	public String getChapterHTMLView(Chapter chapter) {
		return chapterCtrl.getChapterHTMLView(chapter);
	}
	
	public String getVerseText(Integer verse) {
		if (currChapter == null) {
			return "";
		}
		ArrayList<Verse> verses = currChapter.getVerseList();
		if (verses.size() < --verse) {
			return "";
		}
		return StringProc.stripTags(verses.get(verse).getText(), "", true)
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
			+ new OSISLink(currModule, currBook, currChapterNumber, currVerseNumber).getChapterPath()
			+ "." + verse + delimeter1
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
	
	public LinkedHashMap<String, String> search(String query, String fromBook, String toBook) throws OpenModuleException, BookNotFoundException{
		if (currModule == null) {
			return new LinkedHashMap<String, String>();
		} else {
			return bookCtrl.search(currModule, query, fromBook, toBook);
		}
	}

	
//	///////////////////////////////////////////////////////////////////////////
//	// GET LINK OF STRING
//		
	public String getModuleFullName(String moduleID){
		if (currModule == null) {
			return "";
		}
		return currModule.getName();
	}

	public String getBookFullName(String moduleID, String bookID) throws BookNotFoundException, OpenModuleException{
		// Получим модуль по его ID
		Module module;
		try {
			module = getModule(moduleID);
		} catch (OpenModuleException e) {
			return "---";
		}

		if (bookID == "---") {
			return "---";
		}
		Book book = bookCtrl.getBookByID(module, bookID);
		if (book == null) {
			return "---";
		}
		return book.Name;
	}

	public String getBookShortName(String moduleID, String bookID) throws BookNotFoundException, OpenModuleException{
		// Получим модуль по его ID
		Module module;
		try {
			module = getModule(moduleID);
		} catch (OpenModuleException e) {
			return "---";
		}
		if (bookID == "---") {
			return "---";
		}
		Book book = bookCtrl.getBookByID(module, bookID);
		if (book == null) {
			return "---";
		}
		return book.getShortName();
	}

	public String getCurrentLink(){
		return getCurrentLink(true);
	}
	
	public String getCurrentLink(boolean includeModuleID){
		return (includeModuleID ? currModule.ShortName + ": " : "") 
			+ currBook.getShortName() + " " + currChapterNumber;
	}
	
	public CharSequence getModuleName() {
		if (currModule == null) {
			return "";
		} else {
			return currModule.getName();
		}
	}
	
	public CharSequence getHumanBookLink() {
		if (currModule == null || currBook == null) {
			return "";
		}
		String bookLink = currBook.getShortName() + " " + currChapterNumber;
		if (bookLink.length() > 10) {
			int strLenght = bookLink.length();
			bookLink = bookLink.substring(0, 4) + "..." + bookLink.substring(strLenght - 4, strLenght);
		}
		return bookLink;
	}
	
	public OSISLink getCurrentOSISLink(){
		return new OSISLink(currModule, currBook, currChapterNumber, currVerseNumber);
	}
	
	public String getOSIStoHuman(String linkOSIS) throws BookNotFoundException, OpenModuleException {
		String[] param = linkOSIS.split("\\.");
		if (param.length < 3) {
			return "";
		}
		
		String moduleID = param[0];
		String bookID = param[1];
		String chapter = param[2];
		
		Module currModule;
		try {
			currModule = getModule(moduleID);
		} catch (OpenModuleException e) {
			return "";
		}
		Book currBook = bookCtrl.getBookByID(currModule, bookID);
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

	public Boolean isOSISLinkValid(OSISLink link)
	{
		if (link.getPath() == null) {
			return false;
		}

		try {
			getModuleByID(link.getModuleID(), link.getModuleDatasourceID());
		} catch (OpenModuleException e) {
			return false;
		}
		
		return true;
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
				+ currBook.OSIS_ID + "/" + currChapterNumber + "_" + verseLink 
				+ "/" + currModule.ShortName); 
		}
		
		return shareText.toString();
	}


}
