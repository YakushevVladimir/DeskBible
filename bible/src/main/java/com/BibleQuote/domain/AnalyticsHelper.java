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
 * File: AnalyticsHelper.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain;

import android.support.annotation.NonNull;

import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;

public interface AnalyticsHelper {

    String ATTR_ACTION = "action";
    String ATTR_BOOK = "book";
    String ATTR_MODULE = "module";
    String ATTR_OPEN_TAG = "tags";
    String ATTR_LINK = "link";
    String ATTR_QUERY = "query";

    String CATEGORY_ADD_TAGS = "add_tags";
    String CATEGORY_ADD_BOOKMARK = "add_bookmark";
    String CATEGORY_CLICK = "click";
    String CATEGORY_MODULES = "modules";
    String CATEGORY_SEARCH = "search";

    /**
     * Событие открытия места в модуле
     *
     * @param link ссылка на место
     */
    void moduleEvent(@NonNull BibleReference link);

    /**
     * Событие создания закладки на место в модуле
     *
     * @param bookmark созданная закладка
     */
    void bookmarkEvent(@NonNull Bookmark bookmark);

    /**
     * Событие выбора функционала приложения
     *
     * @param action имя выбранного функционала
     */
    void clickEvent(@NonNull String action);

    /**
     * Событие поиска по модулю
     *
     * @param query  поисковый запрос
     * @param module модуль, в котором осуществляется поиск
     */
    void searchEvent(@NonNull String query, String module);
}
