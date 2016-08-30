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
 * File: Book.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.domain.entity;

import com.BibleQuote.managers.BibleBooksID;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 */
public abstract class Book implements Serializable {

	private static final long serialVersionUID = -6348188202419079481L;

	/**
	 * Полное имя книги
	 */
	public String name;

	/**
	 * Краткое имя книги. являющееся первым в списке кратких имен
	 */
	public ArrayList<String> shortNames = new ArrayList<String>();

	/**
	 * Имя книги по классификации OSIS
	 */
	public String OSIS_ID;

	/**
	 * Количество глав в книге
	 */
	public Integer chapterQty; 
	private Module module;
	private ArrayList<String> chapterNumbers = new ArrayList<String>();

	public Book(Module module, String name, String shortNames, int chapterQty) {
		this.name = name;
		this.chapterQty = chapterQty;
		this.module = module;
		setShortNames(shortNames);
		setID();
	}

	/**
	 * @return Возвращает краткое имя книги. являющееся первым в списке кратких имен
	 */
	public String getShortName() {
		return shortNames.get(0);
	}


	public ArrayList<String> getChapterNumbers(Boolean isChapterZero) {
		if (chapterQty > 0 && chapterNumbers.isEmpty()) {
			for (int i = 0; i < chapterQty; i++) {
				chapterNumbers.add("" + (i + (isChapterZero ? 0 : 1)));
			}
		}
		return chapterNumbers;
	}

	public String getID() {
		return OSIS_ID;
	}


	public Module getModule() {
		return module;
	}

	private void setID() {
		OSIS_ID = BibleBooksID.getID(this.shortNames);
		if (OSIS_ID == null) {
			OSIS_ID = this.shortNames.get(0);
		}
	}

	private void setShortNames(String shortNames) {
		String[] names = shortNames.trim().split("\\s+");
		if (names.length == 0) {
			this.shortNames.add((this.name.length() < 4 ? this.name : this.name.substring(0, 3)) + ".");
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
	}


	public Integer getFirstChapterNumber() {
		return module.ChapterZero ? 0 : 1;
	}

	public abstract String getDataSourceID();
}
