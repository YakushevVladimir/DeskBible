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

package com.BibleQuote.dal.repository;

import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.modules.Module;

import java.util.Map;

public interface IModuleRepository<T> {

	/**
	 * Загрузка списка модулей из хранилища без чтения данных.
	 * Модулям устанавливается флаг isClosed=true
	 * <br><font color='red'>Производится полная перезапись в кэш коллекции модулей.</font><br>
	 */
	Map<String, Module> loadFileModules();

	void insertModule(T module);

	void deleteModule(String moduleID);

	void updateModule(T module);

	void loadModule(String path) throws OpenModuleException;

	/**
	 * @return Возвращает коллекцию модулей. Если коллекция модулей пустая, пытается загрузить её из кэш.
	 */
	Map<String, Module> getModules();

	/**
	 * @param moduleID - ShortName модуля
	 * @return Возвращает модуль из коллекции по его ShortName
	 */
	T getModuleByID(String moduleID);

}
