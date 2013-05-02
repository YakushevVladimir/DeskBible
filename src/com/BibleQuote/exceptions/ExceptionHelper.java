package com.BibleQuote.exceptions;

import android.content.Context;
import android.util.Log;
import com.BibleQuote.R;
import com.BibleQuote.utils.NotifyDialog;

public class ExceptionHelper {

	public static void onException(Exception ex, Context context, String TAG) {
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
				context.getResources().getString(R.string.exception_open_module_short),
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
