package com.BibleQuote.managers;

import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.listeners.ChangeChaptersEvent;
import com.BibleQuote.listeners.IChangeChaptersListener.ChangeCode;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OSISLink;
import com.BibleQuote.utils.Task;

public class AsyncOpenChapter extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private ChangeChaptersEvent event = null;
	private Librarian librarian;
	private OSISLink link;
	
	public AsyncOpenChapter(String message, Boolean isHidden, Librarian librarian, OSISLink link) {
		super(message, isHidden);
		this.librarian = librarian;
		this.link = link;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		Log.i(TAG, String.format("Open OSIS link with moduleID=%1$s, bookID=%2$s, chapterNumber=%3$s", 
				link.getModuleID(), link.getBookID(), link.getChapterNumber()));
		Module module;
		try {
			module = librarian.openModule(link.getModuleID(), link.getModuleDatasourceID());
			Book book;
			try {
				book = librarian.openBook(module, link.getBookID());
				Chapter chapter = librarian.openChapter(book, link.getChapterNumber());
				event = new ChangeChaptersEvent(ChangeCode.ChapterAdded, module, book, chapter);
			} catch (BookNotFoundException e) {
				Log.e(TAG, String.format("doInBackground(%1$s)", link), e);
			}
		} catch (ModuleNotFoundException e) {
			Log.e(TAG, String.format("doInBackground(%1$s)", link), e);
		}
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	
	public ChangeChaptersEvent getEvent() {
		return event;
	}
}
