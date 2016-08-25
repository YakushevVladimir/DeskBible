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
 * File: BookmarksTags.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.repository.bookmarks;

/**
 * User: Vladimir
 * Date: 23.10.13
 */
public final class BookmarksTags {
	public static final String BOOKMARKSTAGS_KEY_ID = "_id";
	public static final String BOOKMARKSTAGS_BM_ID = "bm_id";
	public static final String BOOKMARKSTAGS_TAG_ID = "tag_id";

	private BookmarksTags() throws InstantiationException {
		throw new InstantiationException("This class is not for instantiation");
	}

}
