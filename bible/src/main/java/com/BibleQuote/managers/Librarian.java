/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: Librarian.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.BibleQuote.dal.repository.fsHistoryRepository;
import com.BibleQuote.domain.controllers.ILibraryController;
import com.BibleQuote.domain.controllers.ITSKController;
import com.BibleQuote.domain.controllers.modules.IModuleController;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.entity.Verse;
import com.BibleQuote.domain.exceptions.BQUniversalException;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.exceptions.TskNotFoundException;
import com.BibleQuote.domain.textFormatters.ModuleTextFormatter;
import com.BibleQuote.domain.textFormatters.StripTagsTextFormatter;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.managers.history.IHistoryManager;
import com.BibleQuote.managers.history.SimpleHistoryManager;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.modules.LinkConverter;
import com.BibleQuote.utils.share.ShareBuilder;
import com.BibleQuote.utils.share.ShareBuilder.Destination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Librarian {

    private static final String NOT_FOUND = "---";
    private static final String TAG = Librarian.class.getSimpleName();

    private ILibraryController libCtrl;
    private Map<String, String> searchResults = new LinkedHashMap<String, String>();
    private Module currModule;
    private Book currBook;
    private Chapter currChapter;
    private Integer currChapterNumber = -1;
    private Integer currVerseNumber = 1;
    private IHistoryManager historyManager;
    private ITSKController tskCtrl;

    /**
     * Инициализация контроллеров библиотеки, модулей, книг и глав.
     * Подписка на событие ChangeBooksEvent
     */
    public Librarian(@NonNull Context context, @NonNull ILibraryController libCtrl, @NonNull ITSKController tskCtrl) {
        this.libCtrl = libCtrl;
        this.tskCtrl = tskCtrl;
        historyManager = new SimpleHistoryManager(
                new fsHistoryRepository(context.getCacheDir()),
                PreferenceHelper.getHistorySize());
    }

    public String getBaseUrl() {
        if (getCurrModule() == null) {
            return "file:///url_initial_load";
        }
        String dataSourceID = getCurrModule().getDataSourceID();
        int pos = dataSourceID.lastIndexOf('/');
        if (++pos <= dataSourceID.length()) {
            return dataSourceID.substring(0, pos);
        } else {
            return dataSourceID;
        }
    }

    public ArrayList<String> getCleanedVersesText() {
        ArrayList<String> result = new ArrayList<String>();

        if (currModule == null) {
            return result;
        }

        ModuleTextFormatter formatter = new ModuleTextFormatter(currModule, new StripTagsTextFormatter());
        formatter.setVisibleVerseNumbers(false);

        ArrayList<Verse> verses = currChapter.getVerseList();
        for (Verse verse : verses) {
            result.add(formatter.format(verse.getText()));
        }
        return result;
    }

    public Book getCurrBook() {
        return currBook;
    }

    public Chapter getCurrChapter() {
        return currChapter;
    }

    public Integer getCurrChapterNumber() {
        return currChapterNumber;
    }

    public Module getCurrModule() {
        return currModule;
    }

    public Integer getCurrVerseNumber() {
        return currVerseNumber;
    }

    public ArrayList<ItemList> getCurrentModuleBooksList() throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        return getBookItemLists(currModule);
    }

    public BibleReference getCurrentOSISLink() {
        return new BibleReference(getCurrModule(), getCurrBook(), getCurrChapterNumber(), getCurrVerseNumber());
    }

    public LinkedList<ItemList> getHistoryList() {
        return historyManager.getLinks();
    }

    public String getHumanBookLink() {
        if (getCurrBook() == null || getCurrChapter() == null) {
            return "";
        }
        String bookLink = getCurrBook().getShortName() + " " + getCurrChapter().getNumber();
        if (bookLink.length() > 10) {
            int strLenght = bookLink.length();
            bookLink = bookLink.substring(0, 4) + "..." + bookLink.substring(strLenght - 4, strLenght);
        }
        return bookLink;
    }

    public String getModuleFullName() {
        if (getCurrModule() == null) {
            return "";
        }
        return getCurrModule().getName();
    }

    public String getModuleID() {
        if (getCurrModule() == null) {
            return "";
        } else {
            return getCurrModule().getID();
        }
    }

    public String getModuleName() {
        if (getCurrModule() == null) {
            return "";
        } else {
            return getCurrModule().getName();
        }
    }

    /**
     * Возвращает список доступных модулей с Библиями, апокрифами, книгами
     *
     * @return возвращает ArrayList, содержащий модули с книгами Библии и апокрифами
     */
    public ArrayList<ItemList> getModulesList() {
        // Сначала отсортируем список по наименованием модулей
        TreeMap<String, Module> tMap = new TreeMap<String, Module>();
        for (Module currModule : libCtrl.getModules().values()) {
            tMap.put(currModule.getName(), currModule);
        }

        // Теперь создадим результирующий список на основе отсортированных данных
        ArrayList<ItemList> moduleList = new ArrayList<ItemList>();
        for (Module currModule : tMap.values()) {
            moduleList.add(new ItemList(currModule.getID(), currModule.getName()));
        }

        return moduleList;
    }

    public Map<String, String> getSearchResults() {
        return this.searchResults;
    }

    public Locale getTextLocale() {
        return currModule == null
                ? new Locale(Module.DEFAULT_LANGUAGE)
                : new Locale(currModule.getLanguage());
    }

    public void setCurrentVerseNumber(int verse) {
        this.currVerseNumber = verse;
    }

    public void clearHistory() {
        historyManager.clearLinks();
    }

    public Book getBookByID(Module module, String bookID) throws BookNotFoundException, OpenModuleException {
        IModuleController modCtrl = Injector.getModuleController(module);
        return modCtrl.getBookByID(bookID);
    }

    public String getBookFullName(String moduleID, String bookID) throws OpenModuleException {
        try {
            Module module = libCtrl.getModuleByID(moduleID);
            IModuleController modCtrl = Injector.getModuleController(module);
            Book book = modCtrl.getBookByID(bookID);
            return book.getName();
        } catch (OpenModuleException e) {
            Log.e(TAG, e.getMessage());
        } catch (BookNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return NOT_FOUND;
    }

    public String getBookShortName(String moduleID, String bookID) {
        try {
            Module module = libCtrl.getModuleByID(moduleID);
            IModuleController modCtrl = Injector.getModuleController(module);
            Book book = modCtrl.getBookByID(bookID);
            return book.getShortName();
        } catch (BookNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (OpenModuleException e) {
            Log.e(TAG, e.getMessage());
        }
        return NOT_FOUND;
    }

    public Chapter getChapterByNumber(Book book, Integer chapterNumber) throws BookNotFoundException {
        IModuleController modCtrl = Injector.getModuleController(book.getModule());
        return modCtrl.getChapter(book.getID(), chapterNumber);
    }


    ///////////////////////////////////////////////////////////////////////////
    // SHARE

    /**
     * Возвращает список глав книги
     *
     * @throws OpenModuleException
     * @throws BookNotFoundException
     */
    public List<String> getChaptersList(String moduleID, String bookID)
            throws BookNotFoundException, OpenModuleException {
        // Получим модуль по его ID
        Module module = libCtrl.getModuleByID(moduleID);
        IModuleController modCtrl = Injector.getModuleController(module);
        return modCtrl.getChapterNumbers(bookID);
    }

    public LinkedHashMap<String, BibleReference> getCrossReference(BibleReference bReference)
            throws TskNotFoundException, BQUniversalException {

        LinkedHashMap<String, BibleReference> result = new LinkedHashMap<String, BibleReference>();
        for (BibleReference reference : tskCtrl.getLinks(bReference)) {
            Book book;
            try {
                book = getBookByID(getCurrModule(), reference.getBookID());
            } catch (OpenModuleException e) {
                Log.e(TAG, String.format("Error open module %1$s for link %2$s",
                        reference.getModuleID(), reference.getBookID()));
                continue;
            } catch (BookNotFoundException e) {
                Log.e(TAG, String.format("Not found book %1$s in module %2$s",
                        reference.getBookID(), reference.getModuleID()));
                continue;
            }
            BibleReference newReference = new BibleReference(getCurrModule(), book,
                    reference.getChapter(), reference.getFromVerse(), reference.getToVerse());
            result.put(
                    LinkConverter.getOSIStoHuman(newReference),
                    newReference);
        }

        return result;
    }

    public HashMap<BibleReference, String> getCrossReferenceContent(Collection<BibleReference> bReferences) {
        ModuleTextFormatter formatter = new ModuleTextFormatter(currModule, new StripTagsTextFormatter());
        formatter.setVisibleVerseNumbers(false);

        HashMap<BibleReference, String> crossReferenceContent = new HashMap<BibleReference, String>();
        for (BibleReference ref : bReferences) {
            try {
                int fromVerse = ref.getFromVerse();
                int toVerse = ref.getToVerse();
                Chapter chapter = getChapterByNumber(getBookByID(getCurrModule(), ref.getBookID()), ref.getChapter());
                crossReferenceContent.put(ref, chapter.getText(fromVerse, toVerse, formatter));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return crossReferenceContent;
    }

    public ArrayList<ItemList> getModuleBooksList(String moduleID) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        Module module = libCtrl.getModuleByID(moduleID);
        return getBookItemLists(module);
    }

    public Bitmap getModuleImage(String path) {
        if (currModule == null) {
            return null;
        }

        IModuleController modCtrl = Injector.getModuleController(currModule);
        return modCtrl.getBitmap(path);
    }

    public Boolean isBible() {
        return currModule != null && currModule.isBible();
    }

    public Boolean isOSISLinkValid(BibleReference link) {
        if (link.getPath() == null) {
            return false;
        }

        try {
            libCtrl.getModuleByID(link.getModuleID());
        } catch (OpenModuleException e) {
            return false;
        }
        return true;
    }

    public void nextChapter() throws OpenModuleException {
        if (currModule == null || currBook == null) {
            return;
        }

        if (currBook.getChapterQty() > (currChapterNumber + (currModule.isChapterZero() ? 1 : 0))) {
            currChapterNumber++;
            currVerseNumber = 1;
        } else {
            IModuleController modCtrl = Injector.getModuleController(currModule);
            try {
                Book nextBook = modCtrl.getNextBook(currBook.getID());
                if (nextBook != null) {
                    currBook = nextBook;
                    currChapterNumber = currBook.getFirstChapterNumber();
                    currVerseNumber = 1;
                }
            } catch (BookNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public Chapter openChapter(BibleReference link) throws BookNotFoundException, OpenModuleException {
        currModule = libCtrl.getModuleByID(link.getModuleID());
        IModuleController modCtrl = Injector.getModuleController(currModule);
        currBook = modCtrl.getBookByID(link.getBookID());
        currChapter = modCtrl.getChapter(link.getBookID(), link.getChapter());
        currChapterNumber = link.getChapter();
        currVerseNumber = link.getFromVerse();

        historyManager.addLink(new BibleReference(getCurrModule(), getCurrBook(), getCurrChapterNumber(), getCurrVerseNumber()));

        return getCurrChapter();
    }

    public void prevChapter() throws OpenModuleException {
        if (currModule == null || currBook == null) {
            return;
        }

        if (!currChapterNumber.equals(currBook.getFirstChapterNumber())) {
            currChapterNumber--;
            currVerseNumber = 1;
        } else {
            try {
                IModuleController modCtrl = Injector.getModuleController(currModule);
                Book nextBook = modCtrl.getPrevBook(currBook.getID());
                if (nextBook != null) {
                    currBook = nextBook;
                    currChapterNumber = currBook.getChapterQty() - (currModule.isChapterZero() ? 1 : 0);
                    currVerseNumber = 1;
                }
            } catch (BookNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public Map<String, String> search(String query, String fromBook, String toBook) throws OpenModuleException, BookNotFoundException {
        if (getCurrModule() == null) {
            searchResults = new LinkedHashMap<String, String>();
        } else {
            IModuleController moduleCtrl = Injector.getModuleController(currModule);
            searchResults = moduleCtrl.search(currModule.getBookList(fromBook, toBook), query);
        }
        return searchResults;
    }

    public void shareText(Context context, TreeSet<Integer> selectVerses, Destination dest) {
        if (getCurrChapter() == null) {
            return;
        }

        ModuleTextFormatter formatter = new ModuleTextFormatter(currModule, new StripTagsTextFormatter());
        formatter.setVisibleVerseNumbers(false);

        LinkedHashMap<Integer, String> verses = getCurrChapter().getVerses(selectVerses);
        for (Integer key : verses.keySet()) {
            verses.put(key, formatter.format(verses.get(key)));
        }

        ShareBuilder builder = new ShareBuilder(context, getCurrModule(), getCurrBook(), getCurrChapter(), verses);
        builder.share(dest);
    }

    @NonNull
    private ArrayList<ItemList> getBookItemLists(Module module) {
        ArrayList<ItemList> result = new ArrayList<ItemList>();
        if (module == null) {
            return result;
        }

        IModuleController modCtrl = Injector.getModuleController(module);
        for (Book book : modCtrl.getBooks()) {
            result.add(new ItemList(book.getID(), book.getName()));
        }
        return result;
    }
}
