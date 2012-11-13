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

import android.content.Context;
import android.content.Intent;

import com.BibleQuote.R;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Chapter;
import com.BibleQuote.models.Module;

public class ActionSendShare extends BaseShareBuilder {
	
	public ActionSendShare(Context context, Module module, Book book,
			Chapter chapter, LinkedHashMap<Integer, String> verses) {
		this.context = context;
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	@Override
	public void share() {
		InitFormatters();
		if (textFormater == null || referenceFormatter == null) {
			return;
		}
		
		final String share = context.getResources().getString(R.string.share);
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_TEXT, getShareText());
		context.startActivity(Intent.createChooser(send, share));;
	}

}
