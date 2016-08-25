/*
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
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: Module.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 */
public abstract class Module implements Serializable {

    public static final String DEFAULT_LANGUAGE = "ru";

    private static final long serialVersionUID = -499369158022814559L;

    public String shortName = "";
    public String ChapterSign = "";
    public String VerseSign = "";
    public String HtmlFilter = "";
    public boolean ChapterZero;
    public boolean containsStrong;
    public boolean isBible;
    public String defaultEncoding = "utf-8";
    public String language = "ru_RU";
    public String fontName = "";
    public String fontPath = "";

    private Map<String, Book> books = new LinkedHashMap<String, Book>();    // to lazy loading on demand
    private String Name = "";

    public Map<String, Book> getBooks() {
        return books;
    }

    public abstract String getDataSourceID();

    public abstract String getID();

    public String getLanguage() {
        if (language == null || !language.contains("-")) {
            return DEFAULT_LANGUAGE;
        } else {
            return language.substring(0, language.indexOf("-")).toLowerCase();
        }
    }

    public String getName() {
        return Name;
    }

    public void setBooks(Map<String, Book> books) {
        this.books = books;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return this.Name;
    }

    public Book getBook(String bookID) {
        return books.get(bookID);
    }

    public List<String> getBookList(String fromBookID, String toBookID) {
        ArrayList<String> result = new ArrayList<String>();
        boolean startSearch = false;
        for (String bookID : books.keySet()) {
            if (!startSearch) {
                startSearch = bookID.equals(fromBookID);
                if (!startSearch) continue;
            }
            result.add(bookID);
            if (bookID.equals(toBookID)) break;
        }
        return result;

    }
}