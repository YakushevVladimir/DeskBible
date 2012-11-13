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

import android.util.Log;

import com.BibleQuote.utils.Task;

public class AsyncWait extends Task {
	private final String TAG = "AsyncWait";
	private AsyncTaskManager currentAsyncTaskManager;
	

	public AsyncWait(String message, Boolean isHidden, AsyncTaskManager currentAsyncTaskManager) {
		super(message, isHidden);
		this.currentAsyncTaskManager = currentAsyncTaskManager;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			if (currentAsyncTaskManager != null) {
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
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
	
	
}
