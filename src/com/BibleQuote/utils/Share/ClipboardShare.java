package com.BibleQuote.utils.Share;

import java.util.LinkedHashMap;

import android.content.Context;
import android.text.ClipboardManager;

import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;

public class ClipboardShare extends BaseShareBuilder {

	public ClipboardShare(Context context, Module module, Book book,
			Chapter chapter, LinkedHashMap<Integer, String> verses) {
		this.context = context;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	@Override
	public void share() {
		InitFormatters();
		if (textFormater == null || referenceFormatter == null) {
			return;
		}

		ClipboardManager clpbdManager = (ClipboardManager)context.getSystemService("clipboard");
	    if (clpbdManager != null) {
			clpbdManager.setText(getShareText());
	    }
	}

}
