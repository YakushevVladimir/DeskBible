package com.BibleQuote.dal.repository;

import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.OpenModuleException;

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
	 * @throws BookNotFoundException книга с указанным ID отсутсвует в модуле
	 */
	LinkedHashMap<String, String> searchInBook(TModule module, String bookID, String regQuery) throws BookNotFoundException;

}
