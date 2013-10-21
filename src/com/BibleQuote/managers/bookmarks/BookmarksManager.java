package com.BibleQuote.managers.bookmarks;

import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.managers.bookmarks.repository.IBookmarksRepository;
import com.BibleQuote.managers.bookmarks.repository.dbBookmarksTagsRepository;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.managers.tags.repository.ITagRepository;
import com.BibleQuote.managers.tags.repository.dbTagRepository;

import java.util.ArrayList;

public class BookmarksManager {

	private IBookmarksRepository bmRepo;
	private ITagRepository tagRepo = new dbTagRepository();
	public static final String TAGS_DELIMETER = ",";

	public BookmarksManager(IBookmarksRepository repository) {
		this.bmRepo = repository;
	}

	public void add(BibleReference ref) {
		add(ref.getPath(), ref.toString());
	}

	public long add(String OSISLink, String link) {
		return bmRepo.add(new Bookmark(OSISLink, link));
	}

	public void add(BibleReference ref, String tags) {
		add(ref.getPath(), ref.toString(), tags);
	}

	public void add(Bookmark bookmark, String tags) {
		long bmID = bmRepo.add(bookmark);
		ArrayList<Long> tagIDs = getTagsIDs(tags);
		new dbBookmarksTagsRepository().add(bmID, tagIDs);
		tagRepo.deleteEmptyTags();
	}

	public void add(String OSISLink, String link, String tags) {
		long bmID = add(OSISLink, link);
		ArrayList<Long> tagIDs = getTagsIDs(tags);
		new dbBookmarksTagsRepository().add(bmID, tagIDs);
	}

	public void delete(Bookmark bookmark) {
		bmRepo.delete(bookmark);
		tagRepo.deleteEmptyTags();
	}

	public ArrayList<Bookmark> getAll() {
		return bmRepo.getAll();
	}

	public ArrayList<Bookmark> getAll(Tag tag) {
		return bmRepo.getAll(tag);
	}

	public void deleteAll() {
		bmRepo.deleteAll();
		tagRepo.deleteEmptyTags();
	}

	private ArrayList<Long> getTagsIDs(String tags) {
		ArrayList<Long> result = new ArrayList<Long>();
		for (String tag : tags.split(TAGS_DELIMETER)) {
			if (!tag.trim().equals("")) result.add(tagRepo.add(tag));
		}
		return result;
	}
}