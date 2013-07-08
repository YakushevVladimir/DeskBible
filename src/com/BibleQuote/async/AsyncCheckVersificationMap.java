package com.BibleQuote.async;

import android.util.Log;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.Task;

public class AsyncCheckVersificationMap extends Task {
	private final String TAG = "AsyncOpenChapter";

	private Librarian librarian;
	private String toModuleID;
	private Exception exception;
	private Boolean isSuccess;

	public AsyncCheckVersificationMap(String message, Boolean isHidden, Librarian librarian, String toModuleID) {
		super(message, isHidden);
		this.librarian = librarian;
		this.toModuleID = toModuleID;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		isSuccess = false;
		try {
			if (toModuleID != null) {
				Log.i(TAG, String.format("Chech of Versification Map from Current Module to moduleID=%1$s", toModuleID));

				librarian.CheckVersificationMap(toModuleID);
			}

			isSuccess = true;

		} catch (OpenModuleException e) {
			exception = e;
		} catch (BookNotFoundException e) {
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
}
