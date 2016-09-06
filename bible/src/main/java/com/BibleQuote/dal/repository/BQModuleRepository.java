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
 * File: BQModuleRepository.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Book;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.entity.Verse;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.repository.IModuleRepository;
import com.BibleQuote.domain.textFormatters.ModuleTextFormatter;
import com.BibleQuote.entity.modules.BQBook;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.CachePool;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.Logger;
import com.BibleQuote.utils.modules.LanguageConvertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class BQModuleRepository implements IModuleRepository<String, BQModule> {

    private static final String TAG = BQModuleRepository.class.getSimpleName();
    private static final String INI_FILENAME = "bibleqt.ini";
    private static final HashMap<String, String> charsets = new HashMap<String, String>();
    private static final Map<String, Chapter> chapterPool = Collections.synchronizedMap(new CachePool<Chapter>());

    static {
        charsets.put("0", "ISO-8859-1"); // ANSI charset
        charsets.put("1", "US-ASCII"); // DEFAULT charset
        charsets.put("77", "MacRoman"); // Mac Roman
        charsets.put("78", "Shift_JIS"); // Mac Shift Jis
        charsets.put("79", "ms949"); // Mac Hangul
        charsets.put("80", "GB2312"); // Mac GB2312
        charsets.put("81", "Big5"); // Mac Big5
        charsets.put("82", "johab"); // Mac Johab (old)
        charsets.put("83", "MacHebrew"); // Mac Hebrew
        charsets.put("84", "MacArabic"); // Mac Arabic
        charsets.put("85", "MacGreek"); // Mac Greek
        charsets.put("86", "MacTurkish"); // Mac Turkish
        charsets.put("87", "MacThai"); // Mac Thai
        charsets.put("88", "cp1250"); // Mac East Europe
        charsets.put("89", "cp1251"); // Mac Russian
        charsets.put("128", "MS932"); // Shift JIS
        charsets.put("129", "ms949"); // Hangul
        charsets.put("130", "ms1361"); // Johab
        charsets.put("134", "ms936"); // GB2312
        charsets.put("136", "ms950"); // Big5
        charsets.put("161", "cp1253"); // Greek
        charsets.put("162", "cp1254"); // Turkish
        charsets.put("163", "cp1258"); // Vietnamese
        charsets.put("177", "cp1255"); // Hebrew
        charsets.put("178", "cp1256"); // Arabic
        charsets.put("186", "cp1257"); // Baltic
        charsets.put("201", "cp1252"); // Cyrillic charset
        charsets.put("204", "cp1251"); // Russian
        charsets.put("222", "ms874"); // Thai
        charsets.put("238", "cp1250"); // Eastern European
        charsets.put("254", "cp437"); // PC 437
        charsets.put("255", "cp850"); // OEM
    }

    public BQModuleRepository() {
    }

    @Override
    public Bitmap getBitmap(BQModule module, String path) {
        InputStream imageStream = getImageReader(module, path);
        if (imageStream == null) {
            return null;
        }
        return BitmapFactory.decodeStream(imageStream);
    }

    @Override
    public BQModule loadModule(String path) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        if (path.endsWith("zip")) {
            path = path + File.separator + INI_FILENAME;
        }

        BQModule result = null;
        try {
            result = new BQModule(path);
            result.setDefaultEncoding(getModuleEncoding(getReader(result, result.iniFileName)));
            fillModule(result, getReader(result, result.iniFileName));
        } catch (DataAccessException e) {
            Logger.i(TAG, "!!!..Error open module from " + path);
            throw new OpenModuleException(path, result.modulePath);
        }
        return result;
    }

    @Override
    public Chapter loadChapter(BQModule module, String bookID, int chapter) throws BookNotFoundException {
        String chapterID = getChapterID(module, bookID, chapter);
        if (chapterPool.containsKey(chapterID)) {
            return chapterPool.get(chapterID);
        }

        Chapter result = null;
        BufferedReader reader = null;
        try {
            Book book = module.getBook(bookID);
            if (book == null) {
                throw new BookNotFoundException(module.getID(), bookID);
            }
            reader = getReader(module, book.getDataSourceID());
            result = loadChapter(module, book, chapter, reader);
            chapterPool.put(chapterID, result);
        } catch (DataAccessException e) {
            Logger.e(TAG, "Can't load chapters of book with ID = " + bookID, e);
            throw new BookNotFoundException(module.getID(), bookID);

        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Map<String, String> searchInBook(BQModule module, String bookID, String regQuery) throws BookNotFoundException {
        BQBook book = (BQBook) module.getBook(bookID);
        if (book == null) {
            throw new BookNotFoundException(module.getID(), bookID);
        }

        LinkedHashMap<String, String> searchRes = null;
        BufferedReader bReader = null;
        try {
            bReader = getReader(module, book.getDataSourceID());
            searchRes = searchInBook(module, bookID, regQuery, bReader);
        } catch (DataAccessException e) {
            throw new BookNotFoundException(module.getID(), bookID);

        } finally {
            try {
                if (bReader != null) {
                    bReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return searchRes;
    }

    private boolean contains(String text, String query) {
        return Pattern.matches(query, text);
    }

    private void fillModule(BQModule module, BufferedReader bReader)
            throws DataAccessException, BooksDefinitionException, BookDefinitionException {
        if (bReader == null) {
            return;
        }

        String str, htmlFilter = "", key, value;
        ArrayList<String> fullNames = new ArrayList<String>();
        ArrayList<String> pathNames = new ArrayList<String>();
        ArrayList<String> shortNames = new ArrayList<String>();
        ArrayList<Integer> chapterQty = new ArrayList<Integer>();
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
                    module.setShortName(value.replaceAll("\\.", ""));
                } else if (key.equals("chaptersign")) {
                    module.setChapterSign(value.toLowerCase());
                } else if (key.equals("chapterzero")) {
                    module.setChapterZero(value.toLowerCase().contains("y"));
                } else if (key.equals("versesign")) {
                    module.setVerseSign(value.toLowerCase());
                } else if (key.equals("htmlfilter")) {
                    htmlFilter = value;
                } else if (key.equals("bible")) {
                    module.setBible(value.toLowerCase().contains("y"));
                } else if (key.equals("strongnumbers")) {
                    module.setContainsStrong(value.toLowerCase().contains("y"));
                } else if (key.equals("language")) {
                    module.setLanguage(LanguageConvertor.getISOLanguage(value));
                } else if (key.equals("desiredfontname")) {
                    module.setFontName(value);
                    module.setFontPath(value + ".ttf");
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
            String message = String.format("fillModule(%1$s)", module.getDataSourceID());
            android.util.Log.e(TAG, message, e);
            throw new DataAccessException(message);
        } finally {
            try {
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String tagFilter[] = {"p", "b", "i", "em", "strong", "q", "big", "sub", "sup", "h1", "h2", "h3", "h4"};
        ArrayList<String> tagArray = new ArrayList<String>();
        Collections.addAll(tagArray, tagFilter);

        if (!htmlFilter.equals("")) {
            String[] words = htmlFilter.replaceAll("\\W", " ").trim().split("\\s+");
            for (String word : words) {
                if (word.equals("") || tagArray.contains(word)) {
                    continue;
                }
                tagArray.add(word);
            }
        }

        String separator = "";
        for (String tag : tagArray) {
            module.setHtmlFilter(module.getHtmlFilter() + separator + "(" + tag + ")|(/" + tag + ")" + "|(" + tag.toUpperCase() + ")|(/" + tag.toUpperCase() + ")");
            separator = "|";
        }

        for (int i = 0; i < pathNames.size(); i++) {
            String path, fullName, shortName;
            int chapters;
            try {
                path = pathNames.get(i);
                fullName = fullNames.get(i);
                shortName = shortNames.get(i);
                chapters = chapterQty.get(i);
                BQBook book = new BQBook(module, fullName, path,
                        (shortNames.size() > i ? shortName : ""),
                        chapters);
                module.getBooks().put(book.getID(), book);
            } catch (IndexOutOfBoundsException e) {
                String message = String.format(
                        "Incorrect attributes of book #%1$s in module %2$s",
                        ++i, module.getDataSourceID());
                throw new BookDefinitionException(message, module.getDataSourceID(), i);
            }
        }
    }

    /**
     * Получить внутреннее представление ссылки на главу
     *
     * @param module  ссылка на модуль
     * @param bookID  id книги в модуле
     * @param chapter номер главы в книге
     * @return внутреннее представление ссылки на главу
     */
    private String getChapterID(Module module, String bookID, int chapter) {
        return String.format("%s:%s:%s", module.getID(), bookID, chapter);
    }

    private InputStream getImageReader(BQModule module, String path) {
        if (module.isArchive()) {
            return FsUtils.getStreamFromZip(module.getDataSourceID(), path);
        } else {
            return FsUtils.getStream(module.getModulePath(), path);
        }
    }

    private String getModuleEncoding(BufferedReader bReader) {
        String encoding = "cp1251";

        if (bReader == null) {
            return encoding;
        }

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
            android.util.Log.e(TAG, "getModuleEncoding()", e);
            e.printStackTrace();
            return encoding;
        } finally {
            try {
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return encoding;
    }

    private BufferedReader getReader(BQModule module, String dataSourceID) throws DataAccessException {
        return module.isArchive()
                ? FsUtils.getTextFileReaderFromZipArchive(module.modulePath, dataSourceID, module.getDefaultEncoding())
                : FsUtils.getTextFileReader(module.modulePath, dataSourceID, module.getDefaultEncoding());
    }

    private String getSearchQuery(String query) {
        StringBuilder result = new StringBuilder();
        if (query.trim().equals("")) {
            return result.toString();
        }

        String[] words = query.toLowerCase().replaceAll("[^\\s\\w]", "").split("\\s+");
        for (String currWord : words) {
            if (result.length() != 0) {
                result.append("(.)*?");
            }
            result.append(currWord);
        }
        return "((?ui).*?" + result.toString() + ".*?)"; // любые символы в начале и конце
    }

    // ToDo: убрать во внешний обработчик
    private String highlightWords(String query, String verse) {
        String[] words = query.toLowerCase().replaceAll("[^\\s\\w]", "").split("\\s+");
        StringBuilder pattern = new StringBuilder(query.length() + words.length);
        for (String word : words) {
            if (pattern.length() != 0) {
                pattern.append("|");
            }
            pattern.append(word);
        }

        Pattern regex = Pattern.compile("((?ui)" + pattern.toString() + ")");
        Matcher regexMatcher = regex.matcher(verse);
        verse = regexMatcher.replaceAll("<b><font color=\"#6b0b0b\">$1</font></b>");
        return verse;
    }

    private Chapter loadChapter(Module module, Book book, Integer chapterNumber, BufferedReader bReader) {

        ArrayList<String> lines = new ArrayList<String>();
        try {
            String str;
            int currentChapter = book.getFirstChapterNumber();
            String chapterSign = module.getChapterSign();
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
            android.util.Log.e(TAG, String.format("loadChapter(%1$s, %2$s)", book.getID(), chapterNumber), e);
            return null;
        }

        ArrayList<Verse> verseList = new ArrayList<Verse>();
        String verseSign = module.getVerseSign();
        int i = -1;
        for (String currLine : lines) {
            if (currLine.toLowerCase().contains(verseSign)) {
                i++;
                verseList.add(new Verse(i, currLine));
            } else if (verseList.size() > 0) {
                verseList.set(i, new Verse(i, verseList.get(i).getText() + " " + currLine));
            }
        }

        return new Chapter(chapterNumber, verseList);
    }

    private LinkedHashMap<String, String> searchInBook(Module module, String bookID, String query, BufferedReader bReader) {
        LinkedHashMap<String, String> searchRes = new LinkedHashMap<String, String>();

        // Подготовим регулярное выражение для поиска
        String searchQuery = getSearchQuery(query);
        if (searchQuery.equals("")) {
            return searchRes;
        }

        String str;
        StringBuilder bookContent = new StringBuilder(1000);
        try {
            while ((str = bReader.readLine()) != null) {
                bookContent.append(str);
            }
        } catch (IOException e) {
            android.util.Log.e(TAG, String.format("searchInBook(%1$s, %2$s, %3$s)", module.getID(), bookID, searchQuery), e);
            e.printStackTrace();
        }

        String content = bookContent.toString();
        if (!contains(content, searchQuery)) {
            return searchRes;
        }

        android.util.Log.i(TAG, " - Start search in book " + bookID);

        ModuleTextFormatter formatter = new ModuleTextFormatter(module);
        formatter.setVisibleVerseNumbers(false);

        int chapterDev = module.isChapterZero() ? -1 : 0;
        int patternFlags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        String[] chapters = Pattern.compile(module.getChapterSign(), patternFlags).split(content);
        String chapter, verse;
        for (int chapterNumber = 0; chapterNumber < chapters.length; chapterNumber++) {
            chapter = module.getChapterSign() + chapters[chapterNumber];
            if (!contains(chapter, searchQuery)) continue;
            String[] verses = Pattern.compile(module.getVerseSign(), patternFlags).split(chapter);
            for (int verseNumber = 0; verseNumber < verses.length; verseNumber++) {
                verse = module.getVerseSign() + verses[verseNumber];
                if (!contains(verse, searchQuery)) continue;
                searchRes.put(
                        new BibleReference(module.getID(), bookID, chapterNumber - chapterDev, verseNumber).getPath(),
                        highlightWords(query, formatter.format(verse)));
            }
        }

        android.util.Log.i(TAG, " - Cancel search in book " + bookID);

        return searchRes;
    }
}
