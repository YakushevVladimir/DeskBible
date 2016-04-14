/*
 * Copyright (c) 2011-2015 Scripture Software
 * http://www.scripturesoftware.org
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.BibleQuote.controllers;

import com.BibleQuote.entity.modules.Module;
import com.BibleQuote.exceptions.OpenModuleException;

import java.util.Map;

public interface IModuleController {

	/**
	 * Загружает из хранилища список модулей без загрузки их данных. Для каждого из модулей
	 * установлен флаг isClosed = true.
	 *
	 * @return Возвращает TreeMap, где в качестве ключа путь к модулю, а в качестве значения
	 *         closed-модуль
	 */
	Map<String, Module> loadFileModules();


	/**
	 * @return Возвращает TreeMap коллекцию модулей с ключом по Module.ShortName
	 */
	Map<String, Module> getModules();

	/**
	 * Возвращает полностью загруженный модуль из коллекции по его ShortName.
	 * Если модуль в коллекции isClosed, то производит его загрузку
	 * <br/>
	 * <font color='red'>Производится полная перезапись в кэш коллекции модулей
	 * при загрузке closed-модуля</font>
	 * <br/>
	 *
	 * @param moduleID ShortName модуля
	 * @return Возвращает полностью загруженный модуль
	 * @throws com.BibleQuote.exceptions.OpenModuleException - указанный ShortName отсутствует в коллекции или
	 *                             произошла ошибка при попытке загрузить данные closed-модуля
	 */
	Module getModuleByID(String moduleID) throws OpenModuleException;

	void loadModule(String path) throws OpenModuleException;
}
