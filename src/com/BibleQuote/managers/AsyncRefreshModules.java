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

import com.BibleQuote.R;
import com.BibleQuote.utils.NotifyDialog;
import com.BibleQuote.utils.Task;

public class AsyncRefreshModules extends Task {
	//private final String TAG = "AsyncRefreshModules";
	
	private Librarian librarian;
	private String errorMessage = "";
	private Context context;
	
	public AsyncRefreshModules(String message, Boolean isHidden, Librarian librarian, Context context) {
		super(message, isHidden);
		this.librarian = librarian;
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		librarian.loadFileModules();
		errorMessage = librarian.loadModules(context.getResources().getString(R.string.exception_open_module));
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (errorMessage != "") {
			new NotifyDialog(errorMessage, context).show();
		}
	}
}
