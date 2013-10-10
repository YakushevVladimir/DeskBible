package com.BibleQuote.managers.tags;

import com.BibleQuote.managers.bookmarks.repository.dbBookmarksTagsRepository;
import com.BibleQuote.managers.tags.repository.ITagRepository;

import java.util.ArrayList;

/**
 * User: Vladimir
 * Date: 10.10.13
 */
public class TagsManager {

	private ITagRepository tagRepo;

	public TagsManager(ITagRepository tagRepo) {
		this.tagRepo = tagRepo;
	}

	public long add(String tag) {
		return tagRepo.add(tag);
	}

	public int upadate(Tag tag) {
		return tagRepo.update(tag);
	}

	public int delete(Tag tag) {
		return tagRepo.delete(tag);
	}

	public ArrayList<Tag> getAll() {
		return tagRepo.getAll();
	}

	public int deleteAll() {
		return tagRepo.deleteAll();
	}
}
