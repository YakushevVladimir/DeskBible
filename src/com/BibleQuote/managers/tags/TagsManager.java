package com.BibleQuote.managers.tags;

import com.BibleQuote.managers.tags.repository.ITagRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
		return tagRepo.add(tag.trim().toLowerCase());
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

	public LinkedHashMap<Tag, String> getAllWithCount() {
		return tagRepo.getAllWithCount();
	}

	public int deleteAll() {
		return tagRepo.deleteAll();
	}
}
