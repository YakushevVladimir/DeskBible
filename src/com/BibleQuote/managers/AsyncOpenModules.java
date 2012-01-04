package com.BibleQuote.managers;

import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.listeners.ChangeModulesEvent;
import com.BibleQuote.listeners.ChangeModulesEvent.ChangeCode;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.AsyncTaskManager;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.Task;

public class AsyncOpenModules extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private ChangeModulesEvent event;
	private Librarian librarian;
	private Module module;
	private AsyncTaskManager asyncTaskManager;
	
	public AsyncOpenModules(String message, Librarian librarian, Module module, AsyncTaskManager asyncTaskManager) {
		super(message);
		this.librarian = librarian;
		this.module = module;
		this.asyncTaskManager = asyncTaskManager;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		Log.i(TAG, String.format("Load module with moduleID=%1$s", module.getID()));
		try {
			module = librarian.openModule(module.getID());
			event = new ChangeModulesEvent(ChangeCode.ModulesAdded, null);
		} catch (ModuleNotFoundException e) {
			Log.e(TAG, e);
		}
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (librarian.hasClosedModules()) {
			librarian.openModulesAsync(asyncTaskManager, 
					asyncTaskManager.getContext(), 
					asyncTaskManager.getTaskCompleteListener(), 
					asyncTaskManager.isHidden());
		}
	}

	
	public ChangeModulesEvent getEvent() {
		return event;
	}
}
