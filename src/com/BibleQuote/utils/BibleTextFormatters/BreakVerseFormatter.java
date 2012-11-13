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

package com.BibleQuote.utils.BibleTextFormatters;

import java.util.LinkedHashMap;

public class BreakVerseFormatter implements IBibleTextFormatter {
	private LinkedHashMap<Integer, String> verses;

	public BreakVerseFormatter(LinkedHashMap<Integer, String> verses) {
		this.verses = verses;
	}

	@Override
	public String format() {
		StringBuilder shareText = new StringBuilder();

		for (Integer verseNumber : verses.keySet()) {
			if (shareText.length() != 0) {
				shareText.append(" ");
			}
			shareText.append(String.format("%1$s %2$s", verseNumber,
					verses.get(verseNumber))
					+ "\r\n");
		}

		return shareText.toString();
	}

}
