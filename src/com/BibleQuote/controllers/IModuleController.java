package com.BibleQuote.controllers;

import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.modules.Module;

import java.util.Map;

public interface IModuleController {

	/**
	 * Загружает из хранилища список модулей без загрузки их данных. Для каждого из модулей
	 * установлен флаг isClosed = true.
	 *
	 * @return Возвращает TreeMap, где в качестве ключа путь к модулю, а в качестве значения
	 *         closed-модуль
	 */
	public Map<String, Module> loadFileModules();


	/**
	 * @return Возвращает TreeMap коллекцию модулей с ключом по Module.ShortName
	 */
	public Map<String, Module> getModules();

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
	 * @throws OpenModuleException - указанный ShortName отсутствует в коллекции или
	 *                             произошла ошибка при попытке загрузить данные closed-модуля
	 */
	public Module getModuleByID(String moduleID) throws OpenModuleException;

}
