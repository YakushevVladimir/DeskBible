package com.BibleQuote.listeners;

import com.BibleQuote.listeners.IChangeChaptersListener.ChangeCode;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;

public class ChangeChaptersEvent {
	
	public IChangeChaptersListener.ChangeCode code;
	public Module module;
	public Book book;
	public Chapter chapter;
	
	public ChangeChaptersEvent(ChangeCode code, Module module, Book book, Chapter chapter) {
		this.code = code;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
	}	
	
}
