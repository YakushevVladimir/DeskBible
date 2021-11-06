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
 * File: ILibraryController.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.controller;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;

import java.io.File;
import java.util.Map;

/**
 *
 */
public interface ILibraryController {

    /**
     * @return Возвращает коллекцию модулей с ключом по Module.ShortName
     */
    Map<String, BaseModule> getModules();

    /**
     * Получение модуля из коллекции по его ShortName.
     *
     * @param moduleID ShortName модуля
     * @return найденный модуль
     * @throws com.BibleQuote.domain.exceptions.OpenModuleException - указанный ShortName отсутствует в коллекции
     */
    BaseModule getModuleByID(String moduleID) throws OpenModuleException;

    /**
     * Инициализация библиотеки
     */
    void init();

    /**
     * Загружает из хранилища модуль по его пути\
     *
     * @param file файл с модулем для загрузки
     * @throws OpenModuleException по указанному пути модуль не найден
     */
    void loadModule(File file) throws OpenModuleException, BooksDefinitionException, BookDefinitionException;

    /**
     * Загружает из хранилища список модулей без загрузки их данных. Для каждого из модулей
     * установлен флаг isClosed = true.
     *
     * @return Возвращает TreeMap, где в качестве ключа путь к модулю, а в качестве значения
     * closed-модуль
     */
    Map<String, BaseModule> reloadModules();
}
