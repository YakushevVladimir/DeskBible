package com.BibleQuote.utils.Share;

import android.content.Context;
import android.content.Intent;
import com.BibleQuote.R;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Chapter;
import com.BibleQuote.modules.Module;

import java.util.LinkedHashMap;

public class ActionSendShare extends BaseShareBuilder {

	public ActionSendShare(Context context, Module module, Book book,
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

		final String share = context.getResources().getString(R.string.share);
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_TEXT, getShareText());
		context.startActivity(Intent.createChooser(send, share));
		;
	}

}
