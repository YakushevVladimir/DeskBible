package com.BibleQuote.managers;

import android.util.Log;

import com.BibleQuote.utils.AsyncTaskManager;
import com.BibleQuote.utils.Task;

public class AsyncRefreshLibrary extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private Librarian librarian;
	private AsyncTaskManager asyncTaskManager;
	
	public AsyncRefreshLibrary(String message, Librarian librarian, AsyncTaskManager asyncTaskManager) {
		super(message);
		this.librarian = librarian;
		this.asyncTaskManager = asyncTaskManager;
	}
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		Log.d(TAG, "Refresh library...");
		librarian.refreshModules(
				asyncTaskManager, 
				asyncTaskManager.getContext(), 
				asyncTaskManager.getTaskCompleteListener(), 
				asyncTaskManager.isHidden());
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
}
