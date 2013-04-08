package com.BibleQuote.managers.bookmarks;

import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Module;

import java.util.ArrayList;

public class BookmarksManager {

    private IPreferenceRepository repository = new PreferenceRepository();

    public BookmarksManager(IPreferenceRepository repository) {
        this.repository = repository;
    }

    public void add(Module module, Book book, Integer chapter, Integer verse) {
        final BibleReference ref = new BibleReference(module, book, chapter, verse);
        repository.add(ref.toString(), ref.getPath());
    }

    public void add(Librarian lib, Integer verse) {
        lib.setCurrentVerseNumber(verse);
        Module module = lib.getCurrModule();
        Book book = lib.getCurrBook();
        Integer chapter = lib.getCurrChapterNumber();
        add(module, book, chapter, verse);
    }

    public void delete(Bookmark humanLink) {
        repository.delete(humanLink);
    }

    public ArrayList<Bookmark> getAll() {
        return repository.getAll();
    }

    public String getOSISLink(String humanLink) {
        for (Bookmark bookmark : repository.getAll()) {
            if (bookmark.humanLink.equals(humanLink)) return bookmark.OSISLink;
        }
        return "";
    }

    public void sort() {
        repository.sort();
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}