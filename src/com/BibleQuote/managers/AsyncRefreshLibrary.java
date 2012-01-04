package com.BibleQuote.managers;

import android.util.Log;

import com.BibleQuote.utils.Task;

public class AsyncRefreshLibrary extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private Librarian librarian;
	
	public AsyncRefreshLibrary(String message, Librarian librarian) {
		super(message);
		this.librarian = librarian;
	}
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		Log.d(TAG, "Refresh library...");
		librarian.refreshModules(null);		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
}
