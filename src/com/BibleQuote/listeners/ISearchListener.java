package com.BibleQuote.listeners;

public interface ISearchListener {
	public enum SearchCode {
		Found,
		NotFound
	}

	void onSearchInLibrary(SearchInLibraryEvent event);
}
