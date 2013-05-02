package com.BibleQuote.listeners;

import com.BibleQuote.listeners.IChangeBooksListener.ChangeCode;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Module;

import java.util.LinkedHashMap;

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
