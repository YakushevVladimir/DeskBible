package com.BibleQuote.managers;

import android.content.Context;

import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;

public class AsyncManager {
	
	private AsyncTaskManager mAsyncTaskManager = null;  
	//private Map<Task, AsyncTaskManager> pool = Collections.synchronizedMap(new LinkedHashMap<Task, AsyncTaskManager>());
	
	public void setupTask(Object taskObject, OnTaskCompleteListener taskCompleteListener) {
		if (taskObject instanceof Task) {
			Context context = (Context)taskCompleteListener;
			Task task = (Task) taskObject;
			AsyncTaskManager newAsyncTaskManager = new AsyncTaskManager(context, taskCompleteListener);
			//pool.put(task, newAsyncTaskManager);
//			if (mAsyncTaskManager != null && mAsyncTaskManager.isWorking()) {
//				ProgressDialog ourProgress = ProgressDialog.show(context, null, "Please wait ...", true, false);
//				while(true) {
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						break;
//					}
//					if(!mAsyncTaskManager.isWorking()) {
//						break;
//					}
//				}
//				ourProgress.dismiss();
//				ourProgress = null;
//			}
			mAsyncTaskManager = newAsyncTaskManager;
			mAsyncTaskManager.setupTask(task);
		}
	}
	
	public Task retainTask() {
		if (mAsyncTaskManager != null) {
			return (Task) mAsyncTaskManager.retainTask();
		}
		return null;
	}
	
	public void handleRetainedTask(Object taskObject) {
		if (mAsyncTaskManager != null && taskObject instanceof Task) {
			mAsyncTaskManager.handleRetainedTask(taskObject);
		}
	}
	
	public boolean isWorking() {
		return mAsyncTaskManager != null ? mAsyncTaskManager.isWorking() : false;
	}
}
