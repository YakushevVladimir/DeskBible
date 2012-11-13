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

import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Task;

public class AsyncOpenModule extends Task {
	private final String TAG = "AsyncOpenBooks";
	
	private Librarian librarian;
	private BibleReference link;
	private Exception exception;
	private Boolean isSuccess;
	private Module module;
	
	
	public AsyncOpenModule(String message, Boolean isHidden, Librarian librarian, BibleReference link) {
		super(message, isHidden);
		this.librarian = librarian;
		this.link = link;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		isSuccess = false;
		try {
			Log.i(TAG, String.format("Open OSIS link with moduleID=%1$s", link.getModuleID()));
			module = librarian.getModuleByID(link.getModuleID(), link.getModuleDatasourceID());
			
			Log.i(TAG, String.format("Load books for module with moduleID=%1$s", module.getID()));
			librarian.getBookList(module);

			isSuccess = true;
		} catch (OpenModuleException e) {
			//Log.e(TAG, String.format("AsyncOpenBooks(): ", e.toString()), e);
			exception = e;
		} catch (BooksDefinitionException e) {
			exception = e;
		} catch (BookDefinitionException e) {
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

	public Module getModule() {
		return module;
	}

}
