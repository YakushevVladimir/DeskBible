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

import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Task;

public class AsyncLoadModules extends Task {
	private final String TAG = "AsyncLoadModules";
	
	private Librarian librarian;
	private Module nextClosedModule = null;
	private Boolean isReload = false;
	private Exception exception;
	private Boolean isSuccess;
	
	public AsyncLoadModules(String message, Boolean isHidden, Librarian librarian, Boolean isReload) {
		super(message, isHidden);
		this.librarian = librarian;
		this.isReload = isReload;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		isSuccess = false;
		try {
			if (isReload) {
				librarian.loadFileModules();
				isReload = false;
			}
			Module module = librarian.getClosedModule();
			if (module != null) {
				Log.i(TAG, String.format("Open module with moduleID=%1$s", module.getID()));
				module = librarian.getModuleByID(module.getID(), module.getDataSourceID());
				nextClosedModule = librarian.getClosedModule();
			}
			isSuccess = true;
		} catch (OpenModuleException e) {
			//Log.e(TAG, String.format("doInBackground(): %1$s", e.toString()), e);
			exception = e;
		}
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	public Exception getException() {
		return exception;
	}

	public Boolean isSuccess() {
		return isSuccess;
	}
	
	public Module getNextClosedModule() {
		return nextClosedModule;
	}
}
