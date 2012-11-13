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

package com.BibleQuote.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.BibleQuote.entity.BibleBooksID;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public abstract class Book implements Serializable {
	
	private static final long serialVersionUID = -6348188202419079481L;

	/**
	 * Полное имя книги
	 */
	public String Name;
	
	/**
	 * Краткое имя книги. являющееся первым в списке кратких имен
	 */
	public ArrayList<String> ShortNames = new ArrayList<String>();
	
	/**
	 * Имя книги по классификации OSIS 
	 */	
	public String OSIS_ID;
	
	/**
	 * Количество глав в книге
	 */	
	public Integer ChapterQty = 0;
	
	private Module module;
	private ArrayList<String> chapterNumbers = new ArrayList<String>();
	
	
	/**
	 * @return Возвращает краткое имя книги. являющееся первым в списке кратких имен
	 */
	public String getShortName() {
		return ShortNames.get(0);
	}

	
	public ArrayList<String> getChapterNumbers(Boolean isChapterZero) {
		if (ChapterQty > 0 && chapterNumbers.size() == 0) {
			for (int i = 0; i < ChapterQty; i++) {
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
	
	public Book(Module module, String name, String shortNames,  int chapterQty) {
		this.Name = name;
		this.ChapterQty = chapterQty;
		this.module = module;
		
		String[] names = shortNames.trim().split("\\s+");
		if (names.length == 0) {
			this.ShortNames.add((name.length() < 4 ? name : name.substring(0, 3)) + ".");
		} else {
			for (String shortName : names) {
				// В bibleqt.ini может содержаться одно и то же имя
				// с точкой и без. При загрузке модуля точки удаляем,
				// чтобы не было проблемм с ссылками OSIS. Отсюда
				// могут быть не нужные нам дубли имен, избавляемся от них
				if (!this.ShortNames.contains(shortName.trim())) {
					this.ShortNames.add(shortName.trim());
				}
			}
		}
		
		shortNames = this.ShortNames.toString()
			.replace("[", "").replace("]", "");
		OSIS_ID = BibleBooksID.getID(shortNames, ",");
		if (OSIS_ID == null) {
			OSIS_ID = this.ShortNames.get(0);
		}
	}
	
	
	public Integer getFirstChapterNumber() {
		return module.ChapterZero ? 0 : 1;
	}

	
	public abstract Object getDataSourceID();

}
