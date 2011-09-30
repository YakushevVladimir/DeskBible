package com.jBible;

import android.content.Context;

import com.jBible.activity.Reader;
import com.jBible.entity.Librarian;
import com.jBible.utils.UpdateManager;

import greendroid.app.GDApplication;

public class jBibleApp extends GDApplication {
	
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
