package com.BibleQuote.listeners;

import com.BibleQuote.entity.modules.Book;
import com.BibleQuote.entity.modules.Chapter;
import com.BibleQuote.entity.modules.Module;
import com.BibleQuote.listeners.IChangeChaptersListener.ChangeCode;

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
