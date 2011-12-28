package com.BibleQuote._new_.managers;

import com.BibleQuote._new_.listeners.ChangeModulesEvent;
import com.BibleQuote._new_.listeners.ChangeModulesEvent.ChangeCode;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.utils.AsyncTaskManager;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.Task;

public class AsyncOpenModule extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private ChangeModulesEvent event;
	private Librarian librarian;
	private Module module;
	AsyncTaskManager asyncTaskManager;
	
	public AsyncOpenModule(String message, Librarian librarian, Module module, AsyncTaskManager asyncTaskManager) {
		super(message);
		this.librarian = librarian;
		this.module = module;
		this.asyncTaskManager = asyncTaskManager;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		if (module != null) {
			Log.i(TAG, String.format("Load module with moduleID=%1$s", module.getID()));
		}
		try {
			if (module == null) {
				librarian.getModules();
				module = librarian.getCurrentModule();
			}
			if (module != null) {
				module = librarian.openModule(module.getID());
				event = new ChangeModulesEvent(ChangeCode.ModulesAdded, null);
			}
		} catch (NullPointerException e) {
			Log.e(TAG, e);
		}		
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		//librarian.openModules();
		if (!librarian.hasClosedModules()) {
			librarian.openModulesAsync(asyncTaskManager);
		}
	}

	
	public ChangeModulesEvent getEvent() {
		return event;
	}
}
