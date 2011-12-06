package com.BibleQuote._new_.listeners;

public interface ISearchListener {
	public enum SearchCode  {
		Found,
		NotFound
	}

	void onSearchInLibrary(SearchInLibraryEvent event);
}
