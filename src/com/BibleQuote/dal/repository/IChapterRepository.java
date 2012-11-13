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

package com.BibleQuote.dal.repository;

import java.util.Collection;

import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.models.Chapter;

public interface IChapterRepository<TBook> {
    
	/*
	 * Data source related methods
	 * 
	 */
	Collection<Chapter> loadChapters(TBook book) throws BookNotFoundException;
	
	Chapter loadChapter(TBook book, Integer chapterNumber) throws BookNotFoundException;
	
	void insertChapter(Chapter chapter);
    
	void deleteChapter(Chapter chapter);
	
	void updateChapter(Chapter chapter);
	
	/*
	 * Internal cache related methods
	 *
	 */
	Collection<Chapter> getChapters(TBook book);
	
	Chapter getChapterByNumber(TBook book, Integer chapterNumber);

}
