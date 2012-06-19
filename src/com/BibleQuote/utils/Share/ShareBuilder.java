package com.BibleQuote.utils.Share;

import java.util.LinkedHashMap;

import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;

import android.content.Context;

public class ShareBuilder {

	public enum Destination {
		Clipboard, ActionSend
	}
	
	Context context;
	Module module;
	Book book;
	Chapter chapter;
	LinkedHashMap<Integer, String> verses;
	
	public ShareBuilder(Context context, Module module, Book book,
			Chapter chapter, LinkedHashMap<Integer, String> verses) {
		this.context = context;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	public void share(Destination dest) {
		BaseShareBuilder builder = getBuilder(dest);
		if (builder == null) {
			return;
		}
		builder.share();
	}
	
	private BaseShareBuilder getBuilder(Destination dest) {
		if (dest == Destination.ActionSend) {
			return new ActionSendShare(context, module, book, chapter, verses);
		} else if (dest == Destination.Clipboard) {
			return new ClipboardShare(context, module, book, chapter, verses);
		} else {
			return null;
		}
	}

}
