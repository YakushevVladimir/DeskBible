package com.BibleQuote.managers;

import com.BibleQuote.utils.Task;

public class AsyncWait extends Task {
	private final String TAG = "AsyncWait";
	
	private Task task;
	private AsyncTaskManager newAsyncTaskManager;
	private AsyncTaskManager currentAsyncTaskManager;
	
	
	public AsyncWait(String message, Boolean isHidden, Task task, 
			AsyncTaskManager newAsyncTaskManager, AsyncTaskManager currentAsyncTaskManager) {
		super(message, isHidden);
		this.task = task;
		this.newAsyncTaskManager = newAsyncTaskManager;
		this.currentAsyncTaskManager = currentAsyncTaskManager;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		if (task != null && newAsyncTaskManager != null && currentAsyncTaskManager != null) {
			while(true) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					break;
				}
				if(!currentAsyncTaskManager.isWorking()) {
					break;
				}
			}
		}
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		currentAsyncTaskManager = newAsyncTaskManager;
		currentAsyncTaskManager.setupTask(task);	
	}
	
}
