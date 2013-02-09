package com.BibleQuote.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
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


    public Map<String, Book> Books = new LinkedHashMap<String, Book>();	// to lazy loading on demand
	
	// private String Categories = "";
	// private String Copyright = "";
	// private boolean containsOT = false;
	// private boolean containsNT = false;
	// private boolean containsAP = false;
	
	public String toString() {
		return this.Name;
	}
	
	public abstract String getID();
	
	public abstract String getDataSourceID();
	
	public abstract String getModuleFileName();
	
	public abstract Boolean getIsClosed();
	
	public abstract void setIsClosed(Boolean value);

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

    public String getLanguage() {
        int position = language.indexOf("_");
        if (position == -1) {
            return "ru";
        } else {
            return language.substring(0, position).toLowerCase();
        }
    }
}