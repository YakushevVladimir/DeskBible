/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.controllers;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;

import com.BibleQuote.dal.repository.ITskRepository;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BQUniversalException;
import com.BibleQuote.exceptions.TskNotFoundException;
import com.BibleQuote.utils.BibleLinkParser;
import com.BibleQuote.utils.CachePool;

public class TSKController {

	private final int MAX_PULL_SIZE = 10;

	private ITskRepository repository;
	private Map<String, LinkedHashSet<BibleReference>> bCrossReferenceCache = Collections
			.synchronizedMap(new CachePool<LinkedHashSet<BibleReference>>(
					MAX_PULL_SIZE));

	public TSKController(ITskRepository repository) {
		this.repository = repository;
	}

	public LinkedHashSet<BibleReference> getLinks(BibleReference reference) throws TskNotFoundException, BQUniversalException {
		if (bCrossReferenceCache.containsKey(reference.getPath())) {
			return bCrossReferenceCache.get(reference.getPath());
		}

		LinkedHashSet<BibleReference> crossReference = BibleLinkParser.parse(
				reference.getModuleID(), getParallels(reference));
		bCrossReferenceCache.put(reference.getPath(), crossReference);

		return crossReference;
	}

	private String getParallels(BibleReference link) throws TskNotFoundException, BQUniversalException {
		String book = link.getBookID();
		String chapter = String.valueOf(link.getChapter());
		String verse = String.valueOf(link.getFromVerse());
		return repository.getReferences(book, chapter, verse);
	}
}
