package com.BibleQuote._new_.managers;

import com.BibleQuote._new_.listeners.ChangeChaptersEvent;
import com.BibleQuote._new_.listeners.IChangeChaptersListener.ChangeCode;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote._new_.utils.OSISLink;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.Task;

public class AsyncOpenChapter extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private ChangeChaptersEvent event;
	private Librarian librarian;
	private OSISLink link;
	
	public AsyncOpenChapter(String message, Librarian librarian, OSISLink link) {
		super(message);
		this.librarian = librarian;
		this.link = link;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		Log.i(TAG, String.format("Open OSIS link with moduleID=%1$s, bookID=%2$s, chapterNumber=%3$s", 
				link.getModuleID(), link.getBookID(), link.getChapterNumber()));
		try {
			Module module = librarian.openModule(link.getModuleID());
			if (module != null) {
				Book book = librarian.openBook(module, link.getBookID());
				if (book != null) {
					Chapter chapter = librarian.openChapter(book, link.getChapterNumber());
					if (chapter != null) {
						librarian.setCurrentVerse(link.getVerseNumber());
						event = new ChangeChaptersEvent(ChangeCode.ChapterAdded, module, book, chapter);
					}
				}
			}
		} catch (NullPointerException e) {
			Log.e(TAG, e);
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
