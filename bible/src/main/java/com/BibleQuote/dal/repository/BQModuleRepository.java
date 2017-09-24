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
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;

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
import com.BibleQuote.domain.search.algorithm.BoyerMoorAlgorithm;
import com.BibleQuote.domain.search.algorithm.SearchAlgorithm;
import com.BibleQuote.entity.modules.BQBook;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.CachePool;
import com.BibleQuote.utils.FilenameUtils;
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
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class BQModuleRepository implements IModuleRepository<String, BQModule> {

    private static final String TAG = BQModuleRepository.class.getSimpleName();
    private static final String INI_FILENAME = "bibleqt.ini";
    private static final HashMap<String, String> charsets = new HashMap<>();
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

    @Override
    public Bitmap getBitmap(BQModule module, String path) {
        if (path == null) {
            return null;
        } else if (path.startsWith("data") && path.contains("base64")) {
            return getBitmapFromBase64(path);
        } else {
            InputStream imageStream = getImageReader(module, path);
            if (imageStream == null) {
                return null;
            }
            return BitmapFactory.decodeStream(imageStream);
        }
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

        Book book = module.getBook(bookID);
        if (book == null) {
            throw new BookNotFoundException(module.getID(), bookID);
        }

        try (BufferedReader reader = getReader(module, book.getDataSourceID())) {
            Chapter result = loadChapter(module, book, chapter, reader);
            chapterPool.put(chapterID, result);
            return result;
        } catch (DataAccessException | IOException e) {
            Logger.e(TAG, "Can't load chapters of book with ID = " + bookID, e);
            throw new BookNotFoundException(module.getID(), bookID);
        }
    }

    @Override
    @NonNull
    public String getBookContent(BQModule module, String bookID) throws BookNotFoundException {
        BQBook book = (BQBook) module.getBook(bookID);
        if (book == null) {
            throw new BookNotFoundException(module.getID(), bookID);
        }

        StringBuilder bookContent = new StringBuilder(1000);
        try (BufferedReader bReader = getReader(module, book.getDataSourceID())) {
            String str;
            while ((str = bReader.readLine()) != null) {
                bookContent.append(str);
            }
        } catch (IOException | DataAccessException e) {
            e.printStackTrace();
        }

        return bookContent.toString();
    }

    private void fillModule(BQModule module, BufferedReader bReader)
            throws DataAccessException, BooksDefinitionException, BookDefinitionException {
        if (bReader == null) {
            return;
        }

        String str, htmlFilter = "", key, value;
        ArrayList<String> fullNames = new ArrayList<>();
        ArrayList<String> pathNames = new ArrayList<>();
        ArrayList<String> shortNames = new ArrayList<>();
        ArrayList<Integer> chapterQty = new ArrayList<>();
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
                if (pos >= 0) {
                    str = str.substring(0, pos);
                }

                int delimiterPos = str.indexOf("=");
                if (delimiterPos == -1) {
                    continue;
                }

                key = str.substring(0, delimiterPos).trim().toLowerCase();
                delimiterPos++;
                value = delimiterPos >= str.length() ? "" : str.substring(
                        delimiterPos, str.length()).trim();

                switch (key) {
                    case "biblename":
                        module.setName(value);
                        break;
                    case "bibleshortname":
                        module.setShortName(value.replaceAll("\\.", ""));
                        break;
                    case "chaptersign":
                        module.setChapterSign(value.toLowerCase());
                        break;
                    case "chapterzero":
                        module.setChapterZero(value.toLowerCase().contains("y"));
                        break;
                    case "versesign":
                        module.setVerseSign(value.toLowerCase());
                        break;
                    case "htmlfilter":
                        htmlFilter = value;
                        break;
                    case "bible":
                        module.setBible(value.toLowerCase().contains("y"));
                        break;
                    case "strongnumbers":
                        module.setContainsStrong(value.toLowerCase().contains("y"));
                        break;
                    case "language":
                        module.setLanguage(LanguageConvertor.getISOLanguage(value));
                        break;
                    case "desiredfontname":
                        module.setFontName(value);
                        module.setFontPath(value + ".ttf");
                        break;
                    case "pathname":
                        pathNames.add(value);
                        break;
                    case "fullname":
                        fullNames.add(value);
                        break;
                    case "shortname":
                        shortNames.add(value.replaceAll("\\.", ""));
                        break;
                    case "chapterqty":
                        try {
                            chapterQty.add(Integer.valueOf(value));
                        } catch (NumberFormatException e) {
                            chapterQty.add(0);
                        }
                        break;
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
        ArrayList<String> tagArray = new ArrayList<>();
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
        StringBuilder htmlFilterBuilder = new StringBuilder(module.getHtmlFilter());
        for (String tag : tagArray) {
            htmlFilterBuilder
                    .append(separator)
                    .append("(").append(tag).append(")|(/").append(tag).append(")")
                    .append("|(").append(tag.toUpperCase()).append(")|(/").append(tag.toUpperCase()).append(")");
            separator = "|";
        }
        module.setHtmlFilter(htmlFilterBuilder.toString());

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

    private Bitmap getBitmapFromBase64(String path) {
        String[] params = path.split("base64,");
        if (params.length != 2) {
            return null;
        }

        byte[] decodedString = Base64.decode(params[1], Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Получить внутреннее представление ссылки на главу
     *
     * @param module  ссылка на модуль
     * @param bookID  id книги в модуле
     * @param chapter номер главы в книге
     *
     * @return внутреннее представление ссылки на главу
     */
    private String getChapterID(Module module, String bookID, int chapter) {
        return String.format("%s:%s:%s", module.getID(), bookID, chapter);
    }

    private InputStream getImageReader(BQModule module, String path) {
        if (module.isArchive()) {
            return FsUtils.getStreamFromZip(module.getModulePath(), path);
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
                if (pos >= 0) {
                    str = str.substring(0, pos);
                }

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

    private Chapter loadChapter(BQModule module, Book book, Integer chapterNumber, BufferedReader bReader) {

        ArrayList<String> lines = new ArrayList<>();
        try {
            String str;
            int currentChapter = book.getFirstChapterNumber();
            String chapterSign = module.getChapterSign();
            SearchAlgorithm chapterSearch = new BoyerMoorAlgorithm(chapterSign);
            boolean chapterFind = false;
            while ((str = bReader.readLine()) != null) {
                int chapterPos = chapterSearch.indexOf(str);
                if (chapterPos >= 0) {
                    if (chapterFind) {
                        // Тег начала главы может быть не вначале строки.
                        // Возьмем то, что есть до теги начала главы и добавим
                        // к найденным строкам
                        str = str.substring(0, chapterPos);
                        if (str.trim().length() > 0) {
                            lines.add(str);
                        }
                        break;
                    } else if (currentChapter++ == chapterNumber) {
                        chapterFind = true;
                        // Тег начала главы может быть не вначале строки.
                        // Обрежем все, что есть до теги начала главы и добавим
                        // к найденным строкам
                        str = str.substring(chapterPos);
                    }
                }

                if (chapterFind) {
                    lines.add(str);
                }
            }
        } catch (IOException e) {
            android.util.Log.e(TAG, String.format("loadChapter(%1$s, %2$s)", book.getID(), chapterNumber), e);
            return null;
        }

        ArrayList<Verse> verseList = new ArrayList<>();
        String verseSign = module.getVerseSign();
        SearchAlgorithm verseSearch = new BoyerMoorAlgorithm(verseSign);
        int i = -1;
        for (String currLine : lines) {
            currLine = cacheFileFromArchive(module, currLine);
            int versePos = verseSearch.indexOf(currLine);
            if (versePos >= 0) {
                i++;
                verseList.add(new Verse(i, currLine));
            } else if (verseList.size() > 0) {
                // стих может занимать несколько строк
                verseList.set(i, new Verse(i, verseList.get(i).getText() + " " + currLine));
            }
        }

        return new Chapter(chapterNumber, verseList);
    }

    String cacheFileFromArchive(BQModule module, String currLine) {
        if (!module.isArchive()) {
            return currLine;
        }

        Pattern pattern = Pattern.compile("<img.+?src\\s*?=\\s*['\"](.+?)[\"']>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(currLine);
        if (!matcher.find() && matcher.groupCount() < 2) {
            return currLine;
        }

        StringBuilder result = new StringBuilder(currLine);
        for (int i = 1; i < matcher.groupCount() + 1; i++) {
            String path = matcher.group(i);
            if (path == null || path.isEmpty()) {
                continue;
            }

            try (InputStream imageStream = getImageReader(module, path)) {
                byte[] bytes = FsUtils.getBytes(imageStream);
                if (bytes == null) {
                    continue;
                }

                String data = Base64.encodeToString(bytes, Base64.NO_WRAP);
                String ext = FilenameUtils.getExtension(path);
                result.replace(matcher.start(i), matcher.end(i),
                        String.format(Locale.US, "data:image/%s;base64,%s", ext, data));
            } catch (Exception ex) {
                Logger.e(this, "", ex);
                return currLine;
            }
        }

        return result.toString();
    }
}