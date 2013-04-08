package com.BibleQuote.managers.bookmarks;

import com.BibleQuote.entity.BibleReference;

import java.util.ArrayList;

public class BookmarksManager {

    private IPreferenceRepository repository = new PreferenceRepository();

    public BookmarksManager(IPreferenceRepository repository) {
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