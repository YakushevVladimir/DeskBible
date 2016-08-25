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
 * File: TagsManager.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.managers.tags;

import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.repository.ITagRepository;

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
