package com.BibleQuote.modules;

import com.BibleQuote.entity.BibleBooksID;

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
	public Integer chapterQty = 0;

	private Module module;
	private ArrayList<String> chapterNumbers = new ArrayList<String>();


	/**
	 * @return Возвращает краткое имя книги. являющееся первым в списке кратких имен
	 */
	public String getShortName() {
		return shortNames.get(0);
	}


	public ArrayList<String> getChapterNumbers(Boolean isChapterZero) {
		if (chapterQty > 0 && chapterNumbers.size() == 0) {
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

	public Book(Module module, String name, String shortNames, int chapterQty) {
		this.name = name;
		this.chapterQty = chapterQty;
		this.module = module;
		setShortNames(shortNames);
		setID();
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


	public abstract Object getDataSourceID();

}
