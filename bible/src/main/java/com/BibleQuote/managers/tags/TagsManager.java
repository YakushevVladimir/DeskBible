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
 * File: TagsManager.java
 *
 * Created by Vladimir Yakushev at 11/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.managers.tags;

import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.entity.TagWithCount;
import com.BibleQuote.domain.repository.ITagsRepository;

import java.util.List;

/**
 * User: Vladimir
 * Date: 10.10.13
 */
public class TagsManager {

	private ITagsRepository tagsRepository;

	public TagsManager(ITagsRepository tagsRepository) {
		this.tagsRepository = tagsRepository;
	}

	public boolean delete(Tag tag) {
		return tagsRepository.deleteTag(tag.name);
	}

	public List<TagWithCount> getAllWithCount() {
		return tagsRepository.getTagsWithCount();
	}
}
