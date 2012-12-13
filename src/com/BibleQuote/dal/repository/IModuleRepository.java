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
