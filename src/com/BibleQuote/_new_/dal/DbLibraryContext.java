package com.BibleQuote._new_.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbLibraryContext extends DbContext {
	
	private SQLiteDatabase db;
	
	public DbLibraryContext(Context context) {
		db = context.openOrCreateDatabase("library.db", Context.MODE_PRIVATE, null);
	}

	public SQLiteDatabase getDB() {
		return db;
	}
	
}
