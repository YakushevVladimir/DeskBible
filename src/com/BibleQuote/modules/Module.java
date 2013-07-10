package com.BibleQuote.modules;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 */
public abstract class Module implements Serializable {

	private static final long serialVersionUID = -499369158022814559L;

	private String Name = "";
	public String ShortName = "";
	public String ChapterSign = "";
	public String VerseSign = "";
	public String HtmlFilter = "";
	public boolean ChapterZero = false;
	public boolean containsStrong = false;
	public boolean isBible = false;
	public String defaultEncoding = "utf-8";
	public String language = "ru_RU";

	protected VersificationMap versificationMap = null;


	public Map<String, Book> Books = new LinkedHashMap<String, Book>();    // to lazy loading on demand

	// public String Categories = "";
	// public String Copyright = "";
	// public boolean containsOT = false;
	// public boolean containsNT = false;
	// public boolean containsAP = false;

	public String toString() {
		return this.Name;
	}

	public abstract String getID();

	public abstract String getDataSourceID();

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getLanguage() {
		if (language == null || language.indexOf("-") == -1) {
			return "ru";
		} else {
			return language.substring(0, language.indexOf("-")).toLowerCase();
		}
	}

	public abstract VersificationMap getVersificationMap();
}