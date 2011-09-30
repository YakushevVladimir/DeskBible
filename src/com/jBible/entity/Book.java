/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jBible.entity;

import java.util.ArrayList;


public class Book {

	private String name;
	private ArrayList<String> shortNames = new ArrayList<String>();
	private String OSIS_ID;
	private String pathName;
	private Integer chapterQty = 0;
	private String encoding = "UTF-8";
	
	public Book(String name, String pathName, String shortNames,  int chapterQty){
		this.name = name;
		this.pathName = pathName;
		this.chapterQty = chapterQty;
		
		String[] names = shortNames.trim().split(" ");
		if (names.length == 0) {
			this.shortNames.add((name.length() < 4 ? name : name.substring(0, 3)) + ".");
		} else {
			for (String shortName : names) {
				// В bibleqt.ini может содержаться одно и то же имя
				// с точкой и без. При загрузке модуля точки удаляем,
				// чтобы не было проблемм с ссылками OSIS. Отсюда
				// могут быть не нужные нам дубли имен, избавляемся от них
				if (!this.shortNames.contains(shortName.trim())) {
					this.shortNames.add(shortName.trim());
				}
			}
		}
		
		shortNames = this.shortNames.toString()
			.replace("[", "").replace("]", "");
		OSIS_ID = BibleBooksID.getID(shortNames, ",");
		if (OSIS_ID == null) {
			OSIS_ID = this.shortNames.get(0);
		}
	}

	/**
	 * @return Возвращает полное имя книги
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return Возвращает краткое имя книги. являющееся первым в списке кратких имен
	 */
	public String getShortName() {
		return this.shortNames.get(0);
	}

	/**
	 * @return Возвращает имя книги по классификации OSIS 
	 */
	public String getBookID() {
		return OSIS_ID;
	}

	/**
	 * @return Возвращает путь к файлу с книгой
	 */
	public String getPath() {
		return this.pathName;
	}

	/**
	 * @return Возвращает количество глав в книге
	 */
	public int getChapterQty() {
		return this.chapterQty;
	}
	
	/**
	 * Устанавливает кодировку в которой необходимо читать файл содержащий книгу
	 * @param encoding - Кодировка книги
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return Возвращает кодировку файла с данной книгой
	 */
	public String getEncoding() {
		return encoding;
	}
}
