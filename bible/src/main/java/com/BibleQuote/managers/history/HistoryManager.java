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
 * File: HistoryManager.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.managers.history;

import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.repository.IHistoryRepository;
import com.BibleQuote.entity.ItemList;

import java.util.LinkedList;

public class HistoryManager implements IHistoryManager {

	private final int HISTORY_LENGHT;
	private IHistoryRepository repository;

	public HistoryManager(IHistoryRepository repository, int lenght) {
		this.repository = repository;
		this.HISTORY_LENGHT = lenght;
	}

	public synchronized void addLink(BibleReference link) {
		String humanLink = String.format("%1$s: %2$s %3$s:%4$s",
				link.getModuleID(), link.getBookFullName(),
				link.getChapter(), link.getFromVerse());
		ItemList newItem = new ItemList(link.getPath(), humanLink);

		LinkedList<ItemList> history = getLinks();
		if (history.contains(newItem)) {
			history.remove(newItem);
		}
		history.addFirst(newItem);

		while (history.size() > this.HISTORY_LENGHT) {
			history.removeLast();
		}

		repository.save(history);
	}

	public synchronized LinkedList<ItemList> getLinks() {
		try {
			return repository.load();
		} catch (DataAccessException e) {
			return new LinkedList<ItemList>();
		}
	}

	@Override
	public void clearLinks() {
		LinkedList<ItemList> history = new LinkedList<ItemList>();
		repository.save(history);
	}
}
