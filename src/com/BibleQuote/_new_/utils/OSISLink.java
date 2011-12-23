package com.BibleQuote._new_.utils;

import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.utils.Log;

public class OSISLink {

	private final String TAG = "LinkOSIS";
	private String OSISLinkPath = null;
	private String moduleID = null;
	private String bookID = null;
	private Integer chapterNumber;
	
	public OSISLink(String OSISLinkPath) {
		if (OSISLinkPath != null) {
			String[] linkParam = OSISLinkPath.split("\\.");
			try {
				moduleID = linkParam[0];
				bookID = linkParam[1];
				chapterNumber = 1;
				try {
					chapterNumber = Integer.parseInt(linkParam[2]);
				} catch (NumberFormatException  e) {}
			} catch (Exception e) {
				Log.e(TAG, e);
			}
		}
	}

	public OSISLink(String moduleID, String bookID, Integer chapterNumber)
	{
		this.moduleID = moduleID;
		this.bookID = bookID;
		this.chapterNumber = chapterNumber;
	}
	
	public OSISLink(Module module, Book book, Integer chapterNumber)
	{
		this.moduleID = module == null ? null : module.getID();
		this.bookID = book == null ? null : book.getID();
		this.chapterNumber = chapterNumber;
	}
	
	public String getPath()
	{
		if (OSISLinkPath == null) {
			if (moduleID == null || bookID == null) {
				OSISLinkPath = null;
			} else {
				OSISLinkPath = String.format("%1$s.%2$s.%3$s", moduleID, bookID, chapterNumber);
			}
		}
		return OSISLinkPath;
	}
	
	public String getModuleID() {
		return moduleID;
	}

	public String getBookID() {
		return bookID;
	}

	public Integer getChapterNumber() {
		return chapterNumber;
	}

}
