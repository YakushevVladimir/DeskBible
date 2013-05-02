package com.BibleQuote.managers.bookmarks;

import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.managers.bookmarks.repository.IBookmarksRepository;

import java.util.ArrayList;

public class BookmarksManager {

	private IBookmarksRepository repository;

	public BookmarksManager(IBookmarksRepository repository) {
		this.repository = repository;
	}

	public void add(BibleReference ref) {
		repository.add(new Bookmark(ref.getPath(), ref.toString()));
	}

	public void delete(Bookmark bookmark) {
		repository.delete(bookmark);
	}

	public ArrayList<Bookmark> getAll() {
		return repository.getAll();
	}

	public void sort() {
		repository.sort();
	}

	public void deleteAll() {
		repository.deleteAll();
	}
}