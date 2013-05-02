package com.BibleQuote.async;

import android.util.Log;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.modules.Module;
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
			module = librarian.getModuleByID(link.getModuleID());

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
