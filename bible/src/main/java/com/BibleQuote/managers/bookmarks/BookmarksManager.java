/*
 * Copyright (C) 2011 Scripture Software
 *
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
 * Project: BibleQuote-for-Android
 * File: BookmarksManager.java
 *
 * Created by Vladimir Yakushev at 11/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.managers.bookmarks;

import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.domain.repository.ITagsRepository;

import java.util.List;

public class BookmarksManager {

    private ITagsRepository tagsRepository;
    private IBookmarksRepository bookmarksRepository;

    public BookmarksManager(IBookmarksRepository repository, ITagsRepository tagsRepository) {
        this.bookmarksRepository = repository;
        this.tagsRepository = tagsRepository;
    }

    public boolean add(Bookmark bookmark) {
        long bmID = bookmarksRepository.add(bookmark);
        if (bmID == -1) { // при добавлении закладки получили ошибку
            return false;
        }
        tagsRepository.deleteTags(bmID); // удаляем старые теги
        tagsRepository.addTags(bmID, bookmark.tags); // записываем новые
        return true;
    }

    public void delete(Bookmark bookmark) {
        bookmarksRepository.delete(bookmark);
        tagsRepository.deleteTags(bookmark.id);
    }

    public List<Bookmark> getAll() {
        return bookmarksRepository.getAll(null);
    }

    public List<Bookmark> getAll(Tag tag) {
        return bookmarksRepository.getAll(tag);
    }
}