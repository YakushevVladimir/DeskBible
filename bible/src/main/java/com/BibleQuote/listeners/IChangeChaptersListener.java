package com.BibleQuote.listeners;

public interface IChangeChaptersListener {
	public enum ChangeCode {
		ChapterAdded
	}

	public void onChangeChapters(ChangeChaptersEvent event);
}
