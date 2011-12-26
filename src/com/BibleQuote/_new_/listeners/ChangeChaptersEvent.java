package com.BibleQuote._new_.listeners;

import com.BibleQuote._new_.listeners.IChangeChaptersListener.ChangeCode;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.Module;

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
