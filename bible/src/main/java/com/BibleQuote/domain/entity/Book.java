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
 * File: Book.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.entity;

import androidx.annotation.NonNull;

import com.BibleQuote.managers.BibleBooksID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 */
public abstract class Book implements Serializable {

    private static final long serialVersionUID = -6348188202419079481L;

    private final String name;
    private String osisId;
    private final List<String> shortNames;
    private final Integer chapterQty;
    private final boolean hasChapterZero;

    public Book(String name, String shortNames, int chapterQty, boolean hasChapterZero) {
        this.name = name;
        this.chapterQty = chapterQty;
        this.hasChapterZero = hasChapterZero;
        this.shortNames = getShortNames(shortNames);
    }

    /**
     * Количество глав в книге
     */
    public Integer getChapterQty() {
        return chapterQty;
    }

    public abstract String getDataSourceID();

    public int getFirstChapterNumber() {
        return hasChapterZero ? 0 : 1;
    }

    public String getID() {
        return getOSIS_ID();
    }

    public int getLastChapterNumber() {
        return chapterQty - (hasChapterZero ? 1 : 0);
    }

    /**
     * Полное имя книги
     */
    public String getName() {
        return name;
    }

    /**
     * Имя книги по классификации OSIS
     */
    public String getOSIS_ID() {
        if (osisId == null) {
            if (shortNames.size() == 0) {
                throw new IllegalStateException("Short names not found");
            }

            osisId = BibleBooksID.getID(shortNames);
            if (osisId == null) {
                osisId = shortNames.get(0);
            }
        }
        return osisId;
    }

    /**
     * @return Возвращает краткое имя книги. являющееся первым в списке кратких имен
     */
    public String getShortName() {
        return getShortNames().get(0);
    }

    /**
     * Краткое имя книги. являющееся первым в списке кратких имен
     */
    private List<String> getShortNames() {
        return shortNames;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return hasChapterZero == book.hasChapterZero
                && name.equals(book.name)
                && osisId.equals(book.osisId)
                && Objects.equals(shortNames, book.shortNames)
                && chapterQty.equals(book.chapterQty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, osisId, shortNames, chapterQty, hasChapterZero);
    }

    @NonNull
    private String createShortName() {
        return (name.length() < 4 ? name : name.substring(0, 3)) + ".";
    }

    private List<String> getShortNames(String shortNames) {
        final List<String> result = new ArrayList<>();
        String[] names = shortNames == null ? new String[]{} : shortNames.trim().split("\\s+");
        if (names.length == 0) {
            result.add(createShortName());
        } else {
            for (String shortName : names) {
                // В bibleqt.ini может содержаться одно и то же имя
                // с точкой и без. При загрузке модуля точки удаляем,
                // чтобы не было проблемм с ссылками OSIS. Отсюда
                // могут быть не нужные нам дубли имен, избавляемся от них
                if (!result.contains(shortName.trim())) {
                    result.add(shortName.trim());
                }
            }
        }

        return result;
    }
}
