package com.BibleQuote.exceptions;

import android.content.Context;

import com.BibleQuote.R;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.NotifyDialog;

public class ExceptionHelper {

	public static void onOpenModuleException(OpenModuleException ex, Context context, String TAG) {
		String message = String.format(
				context.getResources().getString(R.string.exception_open_module), 
				ex.getModuleId(), ex.getModuleDatasourceId());
		Log.e(TAG, message);
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
