/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: BookmarksManager.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.managers.bookmarks;

import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.domain.repository.IBookmarksTagsRepository;
import com.BibleQuote.domain.repository.ITagRepository;

import java.util.ArrayList;

public class BookmarksManager {

	public static final String TAGS_DELIMETER = ",";

    private IBookmarksTagsRepository bookmarksTagsRepository;
    private IBookmarksRepository bmRepo;
    private ITagRepository tagRepo;

    public BookmarksManager(IBookmarksRepository repository, IBookmarksTagsRepository bookmarksTagsRepository, ITagRepository tagRepo) {
        this.bmRepo = repository;
        this.bookmarksTagsRepository = bookmarksTagsRepository;
        this.tagRepo = tagRepo;
    }

	public void add(BibleReference ref) {
		add(ref.getPath(), ref.toString());
	}

	public long add(String osisLink, String link) {
		return bmRepo.add(new Bookmark(osisLink, link));
	}

	public long add(BibleReference ref, String tags) {
		return add(ref.getPath(), ref.toString(), tags);
	}

	public long add(Bookmark bookmark, String tags) {
		long bmID = add(bookmark);
		ArrayList<Long> tagIDs = getTagsIDs(tags);
        bookmarksTagsRepository.add(bmID, tagIDs);
        tagRepo.deleteEmptyTags();
		return bmID;
	}

	public long add(Bookmark bookmark) {
		long bmID = bmRepo.add(bookmark);
		tagRepo.deleteEmptyTags();
		return bmID;
	}

	public long add(String osisLink, String link, String tags) {
		long bmID = add(osisLink, link);
		ArrayList<Long> tagIDs = getTagsIDs(tags);
        bookmarksTagsRepository.add(bmID, tagIDs);
        return bmID;
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
			if (!tag.trim().equals("")) result.add(tagRepo.add(tag.trim()));
		}
		return result;
	}
}