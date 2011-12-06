package com.BibleQuote._new_.dal;

import android.database.sqlite.SQLiteDatabase;

public class DbContext {
	
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
