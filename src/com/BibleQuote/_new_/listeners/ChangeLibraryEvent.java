package com.BibleQuote._new_.listeners;

import java.util.TreeMap;

import com.BibleQuote._new_.listeners.IChangeListener.ChangeCode;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Module;

public class ChangeLibraryEvent {
	
	public IChangeListener.ChangeCode code;
	public TreeMap<String, Module> modules;
	public TreeMap<String, Book> books;
	
	public ChangeLibraryEvent(ChangeCode code, TreeMap<String, Module> modules) {
		this.code = code;
		this.modules = modules;
	}
}
