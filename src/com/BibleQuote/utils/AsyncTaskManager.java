/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.BibleQuote.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public final class AsyncTaskManager implements IProgressTracker,
		OnCancelListener {

	private OnTaskCompleteListener mTaskCompleteListener;
	private ProgressDialog mProgressDialog;
	private Task mAsyncTask;
	private Context mContext;
	private Boolean mIsHidden;
	
	public void setupTask(Task asyncTask, OnTaskCompleteListener taskCompleteListener, Boolean isHidden, String...params) {
		// Save reference to complete listener (activity)
		mTaskCompleteListener = taskCompleteListener;
		mIsHidden = isHidden;
		mContext = (Context) taskCompleteListener;
		
		// Setup progress dialog
		if (!mIsHidden) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(this);
		} else {
			mProgressDialog = null;
		}
		
		if (mAsyncTask != null) {
			mAsyncTask.cancel(true);
			mAsyncTask = null;
		}		
		
		// Keep task
		mAsyncTask = asyncTask;
		// Wire task to tracker (this)
		mAsyncTask.setProgressTracker(this);
		// Start task
		if (params != null) {
			mAsyncTask.execute(params);
		} else {
			mAsyncTask.execute();
		}
	}

	public void setupTask(Task asyncTask, OnTaskCompleteListener taskCompleteListener, Boolean isHidden) {
		setupTask(asyncTask, taskCompleteListener, isHidden, (String[])null);
	}

	@Override
	public void onProgress(String message) {
		if (mProgressDialog != null) {
			// Show dialog if it wasn't shown yet or was removed on configuration
			// (rotation) change
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}
			// Show current message in progress dialog
			mProgressDialog.setMessage(message);
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// Cancel task
		mAsyncTask.cancel(true);
		
		Task resultTask = mAsyncTask;
		// Reset task
		mAsyncTask = null;
		
		// Notify activity about completion
		mTaskCompleteListener.onTaskComplete(resultTask);
	}

	@Override
	public void onComplete() {
		// Close progress dialog
		if (mProgressDialog != null) mProgressDialog.dismiss();

		Task resultTask = mAsyncTask;
		// Reset task
		mAsyncTask = null;

		// Notify activity about completion
		mTaskCompleteListener.onTaskComplete(resultTask);
	}

	public Object retainTask() {
		// Detach task from tracker (this) before retain
		if (mAsyncTask != null) {
			mAsyncTask.setProgressTracker(null);
		}
		// Retain task
		return mAsyncTask;
	}

	public void handleRetainedTask(Object instance) {
		// Restore retained task and attach it to tracker (this)
		if (instance instanceof Task) {
			mAsyncTask = (Task) instance;
			mAsyncTask.setProgressTracker(this);
		}
	}

	public boolean isWorking() {
		// Track current status
		return mAsyncTask != null;
	}
	
	public Context getContext() {
		return mContext;
	}
}