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
 * File: FragmentModule.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.di.module;

import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.domain.repository.IBookmarksTagsRepository;
import com.BibleQuote.domain.repository.ITagRepository;
import com.BibleQuote.managers.bookmarks.BookmarksManager;
import com.BibleQuote.managers.tags.TagsManager;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    @Provides
    BookmarksManager provideBookmarksManager(IBookmarksRepository bmRepo, IBookmarksTagsRepository bmtRepo,
            ITagRepository tagRepo) {
        return new BookmarksManager(bmRepo, bmtRepo, tagRepo);
    }

    @Provides
    TagsManager provideTagsManager(ITagRepository tagRepository) {
        return new TagsManager(tagRepository);
    }
}
