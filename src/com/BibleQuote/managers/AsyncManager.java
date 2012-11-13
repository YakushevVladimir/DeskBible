/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.managers;

import android.content.Context;

import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;

public class AsyncManager implements OnTaskCompleteListener {

	private static String TAG = "AsyncManager";
	
	private AsyncTaskManager mWaitTaskManager;
	private AsyncTaskManager mAsyncTaskManager;  
	private Task waitTask;	// the task is waiting its execution
	private OnTaskCompleteListener mTaskCompleteListener;
	
	public synchronized void setupTask(Object taskObject, OnTaskCompleteListener taskCompleteListener) {
		Log.i(TAG, "Setup task...");
		if (taskObject instanceof Task && taskCompleteListener instanceof Context) {
			Task newTask = (Task) taskObject;
			mTaskCompleteListener = taskCompleteListener;
			Context context = (Context) mTaskCompleteListener;
			AsyncTaskManager newAsyncTaskManager = new AsyncTaskManager(context, mTaskCompleteListener);
			
			if (isWorking()) {
				// Override the next task only if a new task is a foreground task (with a progress dialog visible)
				if (waitTask == null || !newTask.isHidden()) {
					waitTask = newTask;
				}
				
				if (mWaitTaskManager == null) {
					// Start a wait thread until mAsyncTaskManager has completed
					mWaitTaskManager = new AsyncTaskManager(context, this);
					mWaitTaskManager.setupTask(	new AsyncWait("Please wait ...", false, mAsyncTaskManager));
				}
			} else {
				mAsyncTaskManager = newAsyncTaskManager;
				Task nextTask; 
				if (waitTask != null) {
					nextTask = waitTask;
					waitTask = newTask;
				} else {
					nextTask = newTask;
				}
				mAsyncTaskManager.setupTask(nextTask);
			}
		}
	}
	
	public Task retainTask() {
		if (mWaitTaskManager != null) {
			mWaitTaskManager.retainTask();
			mWaitTaskManager = null;
		}
		waitTask = null;
		if (mAsyncTaskManager != null) {
			Task retainTask = (Task) mAsyncTaskManager.retainTask();
			mAsyncTaskManager = null;
			return retainTask;
		}
		return null;
	}
	
	public void handleRetainedTask(Object taskObject, OnTaskCompleteListener taskCompleteListener) {
		if (taskObject instanceof Task && taskCompleteListener instanceof Context) {
			mTaskCompleteListener = taskCompleteListener;
			mAsyncTaskManager = new AsyncTaskManager((Context) mTaskCompleteListener, mTaskCompleteListener);
			mAsyncTaskManager.handleRetainedTask(taskObject, mTaskCompleteListener);
		}
	}
	
	public boolean isWorking() {
		return mAsyncTaskManager != null ? mAsyncTaskManager.isWorking() : false;
	}

	@Override
	public void onTaskComplete(Task task) {
		if (mWaitTaskManager != null) {
			mWaitTaskManager.retainTask();
			mWaitTaskManager = null;
		}
		try {
			Task newTask = waitTask;
			waitTask = null;
			setupTask(newTask, mTaskCompleteListener);
		} catch (Exception e) {
		}
	}
}
