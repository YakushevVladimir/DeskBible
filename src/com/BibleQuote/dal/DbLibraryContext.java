package com.BibleQuote.dal;

import java.io.File;

import com.BibleQuote.utils.DataConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbLibraryContext extends LibraryContext {
	
	private SQLiteDatabase db;
	
	public DbLibraryContext(File libraryDir, Context context) {
		super(context);
		if (libraryDir != null && !libraryDir.exists()) {
			libraryDir.mkdir();
		}			
        db = SQLiteDatabase.openDatabase(DataConstants.DB_LIBRARY_NAME, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY );
		//db = context.openOrCreateDatabase(DataConstants.DB_LIBRARY_NAME, Context.MODE_PRIVATE, null);
	}

	public SQLiteDatabase getDB() {
		return db;
	}
	
	public void executeAsATransaction(SQLiteDatabase myDatabase, Runnable actions) {
		boolean transactionStarted = false;
		try {
			myDatabase.beginTransaction();
			transactionStarted = true;
		} catch (Throwable t) {
		}
		try {
			actions.run();
			if (transactionStarted) {
				myDatabase.setTransactionSuccessful();
			}
		} finally {
			if (transactionStarted) {
				myDatabase.endTransaction();
			}
		}
	}
}
