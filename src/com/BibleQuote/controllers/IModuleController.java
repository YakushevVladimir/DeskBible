/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.controllers;

import java.util.TreeMap;

import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Module;

public interface IModuleController {
	
	/**
	 * Загружает из хранилища список модулей без загрузки их данных. Для каждого из модулей
	 * установлен флаг isClosed = true.
	 * @return Возвращает TreeMap, где в качестве ключа путь к модулю, а в качестве значения 
	 * closed-модуль
	 */
	public TreeMap<String, Module> loadFileModules();
	
	
    /**
     * @return Возвращает TreeMap коллекцию модулей с ключом по Module.ShortName
     */
	public TreeMap<String, Module> getModules();
	
	
	/**
	 * Возвращает полностью загруженный модуль. Ищет модуль в коллекции
	 * модулей. Если он отсутствует в коллекции производит его загрузку
	 * из хранилища. Для closed-модуля инициируется полная загрузка данных 
	 * модуля и обновления кэш
	 * 
	 * @param moduleID ShortName модуля
	 * @param moduleDatasourceID путь к данным модуля в хранилище
	 * @return Возвращает полностью загруженный модуль
	 * @throws OpenModuleException произошла ошибки при попытке загрузки closed-модуля
	 * из хранилища
	 */
	public Module getModuleByID(String moduleID, String moduleDatasourceID) throws OpenModuleException;
	

	/**
	 * Возвращает полностью загруженный модуль из коллекции по его ShortName. 
	 * Если модуль в коллекции isClosed, то производит его загрузку
	 * <br/>
	 * <font color='red'>Производится полная перезапись в кэш коллекции модулей
	 * при загрузке closed-модуля</font>
	 * <br/>
	 * @param moduleID ShortName модуля
	 * @return Возвращает полностью загруженный модуль
	 * @throws OpenModuleException - указанный ShortName отсутствует в коллекции или
	 * произошла ошибка при попытке загрузить данные closed-модуля
	 */
	public Module getModuleByID(String moduleID) throws OpenModuleException;
	

	/**
	 * @return Возвращает первый closed-модуль из коллекции модулей
	 */
	public Module getClosedModule();
}
