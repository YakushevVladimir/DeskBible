package com.BibleQuote._new_.listeners;

import java.util.LinkedHashMap;

import com.BibleQuote._new_.listeners.IChangeBooksListener.ChangeCode;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Module;

public class ChangeBooksEvent {
	
	public IChangeBooksListener.ChangeCode code;
	public Module module;
	public LinkedHashMap<String, Book> books;
	
	public ChangeBooksEvent(ChangeCode code, Module module, LinkedHashMap<String, Book> books) {
		this.code = code;
		this.books = books;
		this.module = module;
	}
	
}
