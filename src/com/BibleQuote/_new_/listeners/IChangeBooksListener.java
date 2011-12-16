package com.BibleQuote._new_.listeners;

public interface IChangeBooksListener {
	public enum ChangeCode {
		BooksLoaded,
		BooksAdded,
		BooksChanged,
		BooksDeleted
	}
	
	public void onChangeBooks(ChangeBooksEvent event);
}
