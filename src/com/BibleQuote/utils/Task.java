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

import android.os.AsyncTask;

public abstract class Task extends AsyncTask<String, String, Boolean> {

	/**
	 * @uml.property name="mResult"
	 */
	private Boolean mResult;
	/**
	 * @uml.property name="mProgressMessage"
	 */
	private String mProgressMessage;
	/**
	 * @uml.property name="mProgressTracker"
	 * @uml.associationEnd
	 */
	private IProgressTracker mProgressTracker;

	private Boolean mIsHidden = false;

	@Override
	protected abstract Boolean doInBackground(String... arg0);

	@Override
	protected void onPostExecute(Boolean result) {
		// Update result
		mResult = result;
		// And send it to progress tracker
		if (mProgressTracker != null) {
			mProgressTracker.onComplete();
		}
		// Detach from progress tracker
		mProgressTracker = null;
	}

	public Task(String message, Boolean isHidden) {
		mProgressMessage = message;
		mIsHidden = isHidden;
	}

	public void setProgressTracker(IProgressTracker progressTracker) {
		// Attach to progress tracker
		mProgressTracker = progressTracker;
		// Initialize progress tracker with current task state
		if (mProgressTracker != null) {
			mProgressTracker.onProgress(mProgressMessage);
			if (mResult != null) {
				mProgressTracker.onComplete();
			}
		}
	}

	@Override
	protected void onCancelled() {
		// Detach from progress tracker
		mProgressTracker = null;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// Update progress message
		mProgressMessage = values[0];
		// And send it to progress tracker
		if (mProgressTracker != null) {
			mProgressTracker.onProgress(mProgressMessage);
		}
	}

	public Boolean isHidden() {
		return mIsHidden;
	}

}