package com.BibleQuote._new_.listeners;

public interface IChangeListener {
	public enum ChangeCode {
		ModulesChanged,
		BooksChanged
	}
	
	public void onChangeLibrary(ChangeLibraryEvent event);
}
