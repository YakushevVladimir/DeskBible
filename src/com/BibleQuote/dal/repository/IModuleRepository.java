package com.BibleQuote.dal.repository;

import com.BibleQuote.modules.Module;

import java.util.TreeMap;

public interface IModuleRepository<TModuleId, TModule> {
    
	/**
	 * Загрузка списка модулей из хранилища без чтения данных. 
	 * Модулям устанавливается флаг isClosed=true
	 * <br><font color='red'>Производится полная перезапись в кэш коллекции модулей.</font><br>
	 */
    TreeMap<String, Module> loadFileModules();
	
    void insertModule(TModule module);
    
    void deleteModule(String moduleID);

    void updateModule(TModule module);
	
	/**
	 *@return Возвращает коллекцию модулей. Если коллекция модулей пустая, пытается загрузить её из кэш.
	 */
    TreeMap<String, Module> getModules();
    
	/**
	 * 
	 * @param moduleID - ShortName модуля
	 * @return Возвращает модуль из коллекции по его ShortName
	 */
	TModule getModuleByID(String moduleID);

}
