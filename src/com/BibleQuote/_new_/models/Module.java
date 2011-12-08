package com.BibleQuote._new_.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public abstract class Module implements Serializable {
	
	private static final long serialVersionUID = -499369158022814559L;
	
	public String Name = "";
	public String ShortName = "";
	public String ChapterSign = "";
	public String VerseSign = "";
	public String HtmlFilter = "";
	public boolean ChapterZero = false;
	public boolean containsStrong = false;
	public boolean isBible = false;
	public String defaultEncoding = "utf-8";
	
	public LinkedHashMap<String, Book> Books = new LinkedHashMap<String, Book>();
	
	// private String Categories = "";
	// private String Copyright = "";
	// private boolean containsOT = false;
	// private boolean containsNT = false;
	// private boolean containsAP = false;
	
	public String toString() {
		return this.Name;
	}

}