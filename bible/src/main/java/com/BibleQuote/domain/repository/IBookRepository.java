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
 * File: IBookRepository.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.domain.repository;

import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;

import java.util.Collection;
import java.util.LinkedHashMap;

public interface IBookRepository<TModule, TBook> {

	/**
	 * Загружает данные о книгах модуля из хранилища
	 *
	 * @param module Модуль для которого необходимо произвести загрузку книг
	 * @return Возвращает коллекцию Book
	 */
	Collection<TBook> loadBooks(TModule module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException;

	/**
	 * Читает из контекта биьблиотеки текущую кллекцию книг для указанного модуля
	 *
	 * @param module модуль для которого необходимо получить список книг
	 * @return полученную колекцию книг из контекста библиотеки
	 */
	Collection<TBook> getBooks(TModule module);

	/**
	 * @param module модуль в котором производится поиск книги
	 * @param bookID ID книги модуля
	 * @return возвращает Book модуля по её ID или null
	 */
	TBook getBookByID(TModule module, String bookID);

	/**
	 * Производит поиск в указанной книге модуля поиск regQuery
	 *
	 * @param module   модуль. в котором содержится книга
	 * @param bookID   ID книги в которой производится поиск regQuery
	 * @param regQuery строка с искомой фразой
	 * @return возвращает коллекцию, содержащую ссылки на места Писания и содержание отрывков,
	 *         в которых была найдена фраза regQuery
	 * @throws com.BibleQuote.domain.exceptions.BookNotFoundException книга с указанным ID отсутсвует в модуле
	 */
	LinkedHashMap<String, String> searchInBook(TModule module, String bookID, String regQuery) throws BookNotFoundException;

}
