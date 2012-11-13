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

package com.BibleQuote.utils.BibleReferenceFormatter;

import java.util.TreeSet;

import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.PreferenceHelper;

public class FullReferenceFormatter extends ReferenceFormatter implements IBibleReferenceFormatter {

	public FullReferenceFormatter(Module module, Book book, String chapter,
			TreeSet<Integer> verses) {
		super(module, book, chapter, verses);
	}

	@Override
	public String getLink() {
		
		String result = String.format(
				"%1$s %2$s:%3$s", 
				book.Name, chapter, getVerseLink());
		if (PreferenceHelper.addModuleToBibleReference()) {
			result = String.format("%1$s|%2$s", result, module.getID());
		} 
		result = String.format("%1$s-%2$s", result, getOnLineBibleLink());
		return result;
	}

}
