package com.BibleQuote.managers;

import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OSISLink;
import com.BibleQuote.utils.Task;

public class AsyncOpenChapter extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private Librarian librarian;
	private OSISLink link;
	private Exception exception;
	private Boolean isSuccess;
	private Chapter chapter;
	private Integer verseNumber;
	
	public AsyncOpenChapter(String message, Boolean isHidden, Librarian librarian, OSISLink link) {
		super(message, isHidden);
		this.librarian = librarian;
		this.link = link;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		isSuccess = false;
		try {
			Log.i(TAG, String.format("Open OSIS link with moduleID=%1$s, bookID=%2$s, chapterNumber=%3$s, verseNumber=%4$s", 
					link.getModuleID(), link.getBookID(), link.getChapterNumber(), link.getVerseNumber()));

			Module module = librarian.openModule(link.getModuleID(), link.getModuleDatasourceID());
			Book book = librarian.openBook(module, link.getBookID());
			chapter = librarian.openChapter(book, link.getChapterNumber(), link.getVerseNumber());
			isSuccess = true;

		} catch (ModuleNotFoundException e) {
			//Log.e(TAG, String.format("doInBackground(%1$s)", link), e);
			exception = e;
		} catch (BookNotFoundException e) {
			//Log.e(TAG, String.format("doInBackground(%1$s)", link), e);
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

	public Chapter getChapter() {
		return chapter;
	}

	public Integer getVerseNumber() {
		return verseNumber;
	}

}
