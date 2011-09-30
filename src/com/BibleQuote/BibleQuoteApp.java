package com.BibleQuote;

import android.content.Context;

import com.BibleQuote.activity.Reader;
import com.BibleQuote.entity.Librarian;
import com.BibleQuote.utils.UpdateManager;

import greendroid.app.GDApplication;

public class BibleQuoteApp extends GDApplication {
	
	Librarian myLibararian;

	@Override
	public Class<?> getHomeActivityClass() {
		return Reader.class;
	}

	public void Init(Context context) {
		UpdateManager.Init(context);
		myLibararian = new Librarian(context);
	}

	public Librarian getLibrarian() {
		return myLibararian;
	}
}
