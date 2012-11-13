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

package com.BibleQuote.listeners;

import com.BibleQuote.listeners.IChangeChaptersListener.ChangeCode;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;

public class ChangeChaptersEvent {
	
	public IChangeChaptersListener.ChangeCode code;
	public Module module;
	public Book book;
	public Chapter chapter;
	
	public ChangeChaptersEvent(ChangeCode code, Module module, Book book, Chapter chapter) {
		this.code = code;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
	}	
	
}
