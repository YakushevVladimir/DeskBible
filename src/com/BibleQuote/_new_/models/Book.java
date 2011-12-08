package com.BibleQuote._new_.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.BibleQuote.entity.BibleBooksID;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public class Book implements Serializable {
	
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
	
	public ArrayList<String> ChapterNumbers = new ArrayList<String>();
	
	public ArrayList<Chapter> ChapterList;	// to lazy loading on demand
	
	
	public Book(String name, String shortNames,  int chapterQty) {
		this.Name = name;
		this.ChapterQty = chapterQty;
		
		String[] names = shortNames.trim().split(" ");
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
	
	/**
	 * @return Возвращает краткое имя книги. являющееся первым в списке кратких имен
	 */
	public String getShortName() {
		return ShortNames.get(0);
	}

}
