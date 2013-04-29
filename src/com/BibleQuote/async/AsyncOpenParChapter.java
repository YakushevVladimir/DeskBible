package com.BibleQuote.async;

import android.util.Log;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.Task;

public class AsyncOpenParChapter extends Task {
	private final String TAG = "AsyncOpenParChapter";

	private Librarian librarian;
	private BibleReference link;
	private BibleReference ParLink;
	private Exception exception;
	private Boolean isSuccess;

	public AsyncOpenParChapter(String message, Boolean isHidden, Librarian librarian, BibleReference CurrOsisLink, BibleReference linkParTr) {
		super(message, isHidden);
		this.librarian = librarian;
		this.link = CurrOsisLink;
		this.ParLink = linkParTr;
	}
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		isSuccess = false;
		try {
			Log.i(TAG, String.format("Open ParTranslates OSIS link with moduleID=%1$s, bookID=%2$s, chapterNumber=%3$s, verseNumber=%4$s",
					link.getModuleID(), link.getBookID(), link.getChapter(), link.getFromVerse()));

			librarian.openParChapter(link, ParLink);
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
