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
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.controller.IModuleController;
import com.BibleQuote.domain.controller.ITSKController;
import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Verse;
import com.BibleQuote.domain.exceptions.BQUniversalException;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.exceptions.TskNotFoundException;
import com.BibleQuote.domain.textFormatters.BacklightTextFormatter;
import com.BibleQuote.domain.textFormatters.ModuleTextFormatter;
import com.BibleQuote.domain.textFormatters.StripTagsTextFormatter;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.managers.history.IHistoryManager;
import com.BibleQuote.utils.Logger;
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

    public static final String EMPTY_OBJ = "---";

    private static final String TAG = Librarian.class.getSimpleName();
    private Book currBook;
    private Chapter currChapter;
    private Integer currChapterNumber = -1;
    private BaseModule currModule;
    private Integer currVerseNumber = 1;
    private IHistoryManager historyManager;
    private ILibraryController libCtrl;
    private PreferenceHelper preferenceHelper;
    private Map<String, String> searchResults = new LinkedHashMap<>();
    private ITSKController tskCtrl;

    /**
     * Инициализация контроллеров библиотеки, модулей, книг и глав.
     * Подписка на событие ChangeBooksEvent
     */
    public Librarian(@NonNull ILibraryController libCtrl, @NonNull ITSKController tskCtrl,
            @NonNull IHistoryManager historyManager, @NonNull PreferenceHelper preferenceHelper) {
        this.libCtrl = libCtrl;
        this.tskCtrl = tskCtrl;
        this.historyManager = historyManager;
        this.preferenceHelper = preferenceHelper;
    }

    public String getBaseUrl() {
        if (currModule == null) {
            return "file:///url_initial_load";
        }
        String dataSourceID = currModule.getDataSourceID();
        int pos = dataSourceID.lastIndexOf('/');
        if (++pos <= dataSourceID.length()) {
            return dataSourceID.substring(0, pos);
        } else {
            return dataSourceID;
        }
    }

    public ArrayList<String> getCleanedVersesText() {
        ArrayList<String> result = new ArrayList<>();

        if (currModule == null || currChapter == null) {
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

    public BaseModule getCurrModule() {
        return currModule;
    }

    public ArrayList<ItemList> getCurrentModuleBooksList() throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        return getBookItemLists(currModule);
    }

    public BibleReference getCurrentOSISLink() {
        return new BibleReference(currModule, currBook, currChapterNumber, currVerseNumber);
    }

    public LinkedList<ItemList> getHistoryList() {
        return historyManager.getLinks();
    }

    public String getHumanBookLink() {
        if (currBook == null || currChapter == null) {
            return "";
        }
        String bookLink = currBook.getShortName() + " " + currChapter.getNumber();
        if (bookLink.length() > 10) {
            int strLenght = bookLink.length();
            bookLink = bookLink.substring(0, 4) + "..." + bookLink.substring(strLenght - 4, strLenght);
        }
        return bookLink;
    }

    public String getModuleFullName() {
        if (currModule == null) {
            return "";
        }
        return currModule.getName();
    }

    public String getModuleID() {
        if (currModule == null) {
            return "";
        } else {
            return currModule.getID();
        }
    }

    /**
     * Возвращает список доступных модулей с Библиями, апокрифами, книгами
     *
     * @return возвращает ArrayList, содержащий модули с книгами Библии и апокрифами
     */
    public ArrayList<ItemList> getModulesList() {
        // Сначала отсортируем список по наименованием модулей
        TreeMap<String, BaseModule> tMap = new TreeMap<>();
        for (BaseModule currModule : libCtrl.getModules().values()) {
            tMap.put(currModule.getName(), currModule);
        }

        // Теперь создадим результирующий список на основе отсортированных данных
        ArrayList<ItemList> moduleList = new ArrayList<>();
        for (BaseModule currModule : tMap.values()) {
            moduleList.add(new ItemList(currModule.getID(), currModule.getName()));
        }

        return moduleList;
    }

    public Map<String, String> getSearchResults() {
        return this.searchResults;
    }

    public Locale getTextLocale() {
        return currModule == null
                ? new Locale(BaseModule.DEFAULT_LANGUAGE)
                : new Locale(currModule.getLanguage());
    }

    public void setCurrentVerseNumber(int verse) {
        if (currModule != null && currBook != null && currChapter != null) {
            this.currVerseNumber = verse;
            preferenceHelper.saveString("last_read",
                    new BibleReference(currModule, currBook, currChapter.getNumber(), currVerseNumber)
                            .getExtendedPath());
        }
    }

    public void clearHistory() {
        historyManager.clearLinks();
    }

    public Book getBookByID(BaseModule module, String bookID) throws BookNotFoundException, OpenModuleException {
        IModuleController modCtrl = Injector.getModuleController(module);
        return modCtrl.getBookByID(bookID);
    }

    public String getBookFullName(String moduleID, String bookID) throws OpenModuleException {
        try {
            BaseModule module = libCtrl.getModuleByID(moduleID);
            IModuleController modCtrl = Injector.getModuleController(module);
            Book book = modCtrl.getBookByID(bookID);
            return book.getName();
        } catch (OpenModuleException e) {
            Logger.e(TAG, e.getMessage());
        } catch (BookNotFoundException e) {
            Logger.e(TAG, e.getMessage());
        }
        return EMPTY_OBJ;
    }

    public String getBookShortName(String moduleID, String bookID) {
        try {
            BaseModule module = libCtrl.getModuleByID(moduleID);
            IModuleController modCtrl = Injector.getModuleController(module);
            Book book = modCtrl.getBookByID(bookID);
            return book.getShortName();
        } catch (BookNotFoundException e) {
            Logger.e(TAG, e.getMessage());
        } catch (OpenModuleException e) {
            Logger.e(TAG, e.getMessage());
        }
        return EMPTY_OBJ;
    }

    /**
     * Возвращает список глав книги
     *
     * @throws OpenModuleException   указанные модуль не найден или произошла ошибка при его открытии
     * @throws BookNotFoundException указанная книга в модуле не найдена
     */
    public List<String> getChaptersList(String moduleID, String bookID)
            throws BookNotFoundException, OpenModuleException {
        // Получим модуль по его ID
        BaseModule module = libCtrl.getModuleByID(moduleID);
        IModuleController modCtrl = Injector.getModuleController(module);
        return modCtrl.getChapterNumbers(bookID);
    }

    public LinkedHashMap<String, BibleReference> getCrossReference(BibleReference bReference)
            throws TskNotFoundException, BQUniversalException {

        LinkedHashMap<String, BibleReference> result = new LinkedHashMap<>();
        for (BibleReference reference : tskCtrl.getLinks(bReference)) {
            Book book;
            try {
                book = getBookByID(currModule, reference.getBookID());
            } catch (OpenModuleException e) {
                Logger.e(TAG, String.format("Error open module %1$s for link %2$s",
                        reference.getModuleID(), reference.getBookID()));
                continue;
            } catch (BookNotFoundException e) {
                Logger.e(TAG, String.format("Not found book %1$s in module %2$s",
                        reference.getBookID(), reference.getModuleID()));
                continue;
            }
            BibleReference newReference = new BibleReference(currModule, book,
                    reference.getChapter(), reference.getFromVerse(), reference.getToVerse());
            result.put(
                    LinkConverter.getOSIStoHuman(newReference),
                    newReference);
        }

        return result;
    }


    ///////////////////////////////////////////////////////////////////////////
    // SHARE

    public HashMap<BibleReference, String> getCrossReferenceContent(Collection<BibleReference> bReferences) {
        ModuleTextFormatter formatter = new ModuleTextFormatter(currModule, new StripTagsTextFormatter());
        formatter.setVisibleVerseNumbers(false);

        HashMap<BibleReference, String> crossReferenceContent = new HashMap<>();
        for (BibleReference ref : bReferences) {
            try {
                int fromVerse = ref.getFromVerse();
                int toVerse = ref.getToVerse();
                Chapter chapter = getChapterByNumber(getBookByID(currModule, ref.getBookID()), ref.getChapter());
                crossReferenceContent.put(ref, chapter.getText(fromVerse, toVerse, formatter));
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
        }
        return crossReferenceContent;
    }

    public ArrayList<ItemList> getModuleBooksList(String moduleID) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        BaseModule module = libCtrl.getModuleByID(moduleID);
        return getBookItemLists(module);
    }

    public Bitmap getModuleImage(String path) {
        if (currModule == null) {
            return null;
        }

        IModuleController modCtrl = Injector.getModuleController(currModule);
        return modCtrl.getBitmap(path);
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
                    currChapter = null;
                    currChapterNumber = currBook.getFirstChapterNumber();
                    currVerseNumber = 1;
                }
            } catch (BookNotFoundException e) {
                Logger.e(TAG, e.getMessage());
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

        final BibleReference reference = new BibleReference(currModule, currBook, currChapterNumber, currVerseNumber);
        historyManager.addLink(reference);
        preferenceHelper.saveString("last_read", reference.getExtendedPath());

        return currChapter;
    }

    public void prevChapter() throws OpenModuleException {
        if (currModule == null || currBook == null) {
            return;
        }

        if (!currChapterNumber.equals(currBook.getFirstChapterNumber())) {
            currChapterNumber -= 1;
            currVerseNumber = 1;
        } else {
            try {
                IModuleController modCtrl = Injector.getModuleController(currModule);
                Book nextBook = modCtrl.getPrevBook(currBook.getID());
                if (nextBook != null) {
                    currBook = nextBook;
                    currChapter = null;
                    currChapterNumber = currBook.getLastChapterNumber();
                    currVerseNumber = 1;
                }
            } catch (BookNotFoundException e) {
                Logger.e(TAG, e.getMessage());
            }
        }
    }

    public Map<String, String> search(String query, String fromBook, String toBook) throws OpenModuleException, BookNotFoundException {
        if (currModule == null) {
            searchResults = new LinkedHashMap<>();
        } else {
            IModuleController moduleCtrl = Injector.getModuleController(currModule);
            searchResults = moduleCtrl.search(currModule.getBookList(fromBook, toBook), query);

            ModuleTextFormatter formatter = new ModuleTextFormatter(currModule, new StripTagsTextFormatter());
            formatter.setVisibleVerseNumbers(false);
            BacklightTextFormatter textFormatter = new BacklightTextFormatter(formatter, query, "#6b0b0b");
            for (Map.Entry<String, String> entry : searchResults.entrySet()) {
                searchResults.put(entry.getKey(), textFormatter.format(entry.getValue()));
            }
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
        for (Map.Entry<Integer, String> entry : verses.entrySet()) {
            verses.put(entry.getKey(), formatter.format(entry.getValue()));
        }

        ShareBuilder builder = new ShareBuilder(context, currModule, currBook, currChapter, verses);
        builder.share(dest);
    }

    @NonNull
    private ArrayList<ItemList> getBookItemLists(BaseModule module) {
        ArrayList<ItemList> result = new ArrayList<>();
        if (module == null) {
            return result;
        }

        IModuleController modCtrl = Injector.getModuleController(module);
        for (Book book : modCtrl.getBooks()) {
            result.add(new ItemList(book.getID(), book.getName()));
        }
        return result;
    }

    private Chapter getChapterByNumber(Book book, Integer chapterNumber) throws BookNotFoundException {
        IModuleController modCtrl = Injector.getModuleController(currModule);
        return modCtrl.getChapter(book.getID(), chapterNumber);
    }

    private Chapter getCurrChapter() {
        return currChapter;
    }
}
