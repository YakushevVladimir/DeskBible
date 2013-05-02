package com.BibleQuote.async;

import android.content.Context;
import com.BibleQuote.managers.Librarian;
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
