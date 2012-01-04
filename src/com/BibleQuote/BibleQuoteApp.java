/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote;

import greendroid.app.GDApplication;

import com.BibleQuote.activity.Reader;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.UpdateManager;

public class BibleQuoteApp extends GDApplication {
	
	private Librarian myLibararian;

	@Override
	public Class<?> getHomeActivityClass() {
		return Reader.class;
	}

	public void Init() {
		PreferenceHelper.Init(this);
		UpdateManager.Init(this);
		getLibrarian();
	}

	public Librarian getLibrarian() {
		if (myLibararian == null) {
			myLibararian = new Librarian(this);
			
			myLibararian.getModules();
			if (myLibararian.hasClosedModules()) {
				myLibararian.openModules(null);
			}
		}
		return myLibararian;
	}
}
