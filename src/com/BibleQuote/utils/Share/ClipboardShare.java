package com.BibleQuote.utils.Share;

import android.content.Context;
import android.text.ClipboardManager;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Chapter;
import com.BibleQuote.modules.Module;

import java.util.LinkedHashMap;

public class ClipboardShare extends BaseShareBuilder {

	public ClipboardShare(Context context, Module module, Book book,
						  Chapter chapter, LinkedHashMap<Integer, String> verses) {
		this.context = context;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void share() {
		InitFormatters();
		if (textFormater == null || referenceFormatter == null) {
			return;
		}

		ClipboardManager clpbdManager = (ClipboardManager) context.getSystemService("clipboard");
		if (clpbdManager != null) {
			clpbdManager.setText(getShareText());
		}
	}

}
