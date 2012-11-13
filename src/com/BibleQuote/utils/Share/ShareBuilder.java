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

package com.BibleQuote.utils.Share;

import java.util.LinkedHashMap;

import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;

import android.content.Context;

public class ShareBuilder {

	public enum Destination {
		Clipboard, ActionSend
	}
	
	Context context;
	Module module;
	Book book;
	Chapter chapter;
	LinkedHashMap<Integer, String> verses;
	
	public ShareBuilder(Context context, Module module, Book book,
			Chapter chapter, LinkedHashMap<Integer, String> verses) {
		this.context = context;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	public void share(Destination dest) {
		BaseShareBuilder builder = getBuilder(dest);
		if (builder == null) {
			return;
		}
		builder.share();
	}
	
	private BaseShareBuilder getBuilder(Destination dest) {
		if (dest == Destination.ActionSend) {
			return new ActionSendShare(context, module, book, chapter, verses);
		} else if (dest == Destination.Clipboard) {
			return new ClipboardShare(context, module, book, chapter, verses);
		} else {
			return null;
		}
	}

}
