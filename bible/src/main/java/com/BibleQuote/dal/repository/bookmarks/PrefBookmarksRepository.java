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
 * File: PrefBookmarksRepository.java
 *
 * Created by Vladimir Yakushev at 3/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.bookmarks;

import com.BibleQuote.domain.entity.Bookmark;
import com.BibleQuote.domain.entity.Tag;
import com.BibleQuote.domain.repository.IBookmarksRepository;
import com.BibleQuote.utils.PreferenceHelper;

import java.util.ArrayList;

/**
 * User: Vladimir Yakushev
 * Date: 09.04.13
 * Time: 0:26
 */
public class PrefBookmarksRepository implements IBookmarksRepository {

	private static final Byte BOOKMARK_DELIMITER = (byte) 0xFE;
	private static final Byte BOOKMARK_PATH_DELIMITER = (byte) 0xFF;
    private static final String KEY_FAVORITS = "Favorits";

	private PreferenceHelper preferenceHelper;

	public PrefBookmarksRepository(PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public long add(Bookmark bookmark) {
        String fav = preferenceHelper.getString(KEY_FAVORITS);
        preferenceHelper.saveString(KEY_FAVORITS, bookmark.humanLink + BOOKMARK_PATH_DELIMITER + bookmark.OSISLink + BOOKMARK_DELIMITER + fav);
        return 0;
    }

	@Override
	public void delete(Bookmark bookmark) {
        String fav = preferenceHelper.getString(KEY_FAVORITS);
        fav = fav.replaceAll(String.format("%s(.)+?%s", bookmark.humanLink, BOOKMARK_DELIMITER), "");
        preferenceHelper.saveString(KEY_FAVORITS, fav);
    }

	@Override
	public void deleteAll() {
        preferenceHelper.saveString(KEY_FAVORITS, "");
    }

	@Override
	public ArrayList<Bookmark> getAll() {
		ArrayList<Bookmark> result = new ArrayList<>();

        String fav = preferenceHelper.getString(KEY_FAVORITS);
        if (fav.equals("")) {
            return result;
		}

		String[] favs = fav.split(BOOKMARK_DELIMITER.toString());
		for (String currFav : favs) {
			String[] parts = currFav.split(BOOKMARK_PATH_DELIMITER.toString());
			if (parts.length == 2) result.add(new Bookmark(parts[1], parts[0]));
		}
		return result;
	}

	@Override
	public ArrayList<Bookmark> getAll(Tag tag) {
		return new ArrayList<>();
	}
}
