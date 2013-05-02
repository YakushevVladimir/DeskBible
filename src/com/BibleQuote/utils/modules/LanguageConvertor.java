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

package com.BibleQuote.utils.modules;

/**
 * User: Vladimir
 * Date: 10.02.13
 * Time: 0:35
 */
public class LanguageConvertor {
	public static String getISOLanguage(String language) {
		language = language.toLowerCase();
		if (language.equals("русский") || language.equals("russian")) {
			return "ru_RU";
		} else if (language.equals("английский") || language.equals("english")) {
			return "en_US";
		} else if (language.equals("немецкий") || language.equals("deutsch") || language.equals("germany")) {
			return "de_DE";
		} else {
			return language;
		}
	}
}
