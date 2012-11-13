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

package com.BibleQuote.dal.repository;

import java.util.Collection;

import com.BibleQuote.exceptions.OpenModuleException;

public interface IModuleRepository<TModuleId, TModule> {
    
	/**
	 * Загрузка списка модулей из хранилища без чтения данных. 
	 * Модулям устанавливается флаг isClosed=true
	 * <br><font color='red'>Производится полная перезапись в кэш коллекции модулей.</font><br>
	 */
	Collection<TModule> loadFileModules();
	
	/**
	 * Загружает данные модуля по его пути к данным без загрузки данных о книгах.
	 * Производит замещение в коллекции модулей:
	 * удалается из коллекции запись с ключом, содержащим путь к модулю и добюавляется
	 * новая запись, где в качестве ключа ShortName модуля и полностью загруженный
	 * модуль в качестве значения. 
	 * <br><font color='red'>Производится полная перезапись в кэш коллекции модулей.</font><br>
	 * @param moduleDataSourceId путь к модулю
	 * @return Возвращает полностью загруженный модуль
	 * @throws OpenModuleException путь к модулю отсутсвует в коллекции модулей, или
	 * произошла ошибка при попытке загрузить данные модуля
	 */
	TModule loadModuleById(TModuleId moduleDatasourceID) throws OpenModuleException;
	
    void insertModule(TModule module);
    
    void deleteModule(TModule module);

    void updateModule(TModule module);
    
	
	/**
	 *@return Возвращает коллекцию модулей. Если коллекция модулей пустая, пытается загрузить её из кэш.
	 */
	Collection<TModule> getModules();
    
	/**
	 * 
	 * @param moduleID - ShortName модуля
	 * @return Возвращает модуль из коллекции по его ShortName
	 */
	TModule getModuleByID(String moduleID);
	
	/**
	 * @param moduleDatasourceID - путь к данным модуля
	 * @return Вовзращает модуль из коллекции по его пути к данным или null.
	 * Поиск модуля в коллекции производится с флагом IgnoryCase 
	 */
	TModule getModuleByDatasourceID(String moduleDatasourceID);
	
	public TModule getClosedModule();
    
}
