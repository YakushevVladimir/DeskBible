package com.BibleQuote._new_.listeners;

public interface IChangeChaptersListener {
	public enum ChangeCode {
		ChapterAdded
	}
	
	public void onChangeChapters(ChangeChaptersEvent event);
}
