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

package com.BibleQuote.exceptions;

import android.content.Context;
import android.util.Log;

import com.BibleQuote.R;
import com.BibleQuote.utils.NotifyDialog;

public class ExceptionHelper {

	public static void onException(Exception ex, Context context, String TAG){
		String message = ex.getMessage();
		if (message == null) {
			return;
		}
		Log.e(TAG, message);
		new NotifyDialog(message, context).show();	
	}
	
	public static void onOpenModuleException(OpenModuleException ex, Context context, String TAG) {
		String moduleId = ex.getModuleId();
		if (moduleId == null) moduleId = "";
		String moduleDatasourceId = ex.getModuleDatasourceId();
		if (moduleDatasourceId == null) moduleDatasourceId = "";
		
		String message = String.format(
				context.getResources().getString(R.string.exception_open_module), 
				moduleId, moduleDatasourceId);
		Log.e(TAG, message);

		if (moduleId == "" && moduleDatasourceId == "") {
			return;
		} else if (moduleId != "" && moduleDatasourceId != "") {
			// the message is defined above
		} else {
			message = String.format(
					context.getResources().getString(R.string.exception_open_module_short), 
					moduleId != "" ? moduleId : moduleDatasourceId);
		}
		new NotifyDialog(message, context).show();			
	}
	
	public static void onBooksDefinitionException(BooksDefinitionException ex, Context context, String TAG) {
		String message = String.format(
				context.getResources().getString(R.string.exception_books_definition), 
				ex.getModuleDatasourceID(), ex.getBooksCount(), 
				ex.getPathNameCount(), ex.getFullNameCount(), ex.getShortNameCount(), ex.getChapterQtyCount());
		Log.e(TAG, message);
		new NotifyDialog(message, context).show();	
	}
	
	public static void onBookDefinitionException(BookDefinitionException ex, Context context, String TAG) {
		String message = String.format( 
				context.getResources().getString(R.string.exception_book_definition),
				ex.getBookNumber(), ex.getModuleDatasourceID(), 
				ex.getPathName(), ex.getFullName(), ex.getShortName(), ex.getChapterQty());
		Log.e(TAG, message);
		new NotifyDialog(message, context).show();
	}
	
	public static void onBookNotFoundException(BookNotFoundException ex, Context context, String TAG) {
		String message = String.format( 
				context.getResources().getString(R.string.exception_book_not_found),
				ex.getBookID(), ex.getModuleID());
		Log.e(TAG, message);
		new NotifyDialog(message, context).show();
	}
}
