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

package com.BibleQuote.widget.listview.item;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.BibleQuote.R;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.widget.listview.itemview.ItemView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * User: Vladimir Yakushev
 * Date: 03.05.13
 */
public class BookmarkItem extends Item {

	public Bookmark bookmark;
	public String link;
	public String date;

	/**
	 * @hide
	 */
	public BookmarkItem() {
	}

	public BookmarkItem(Bookmark bookmark) {
		this.bookmark = bookmark;
		this.link = bookmark.humanLink;
		this.date = bookmark.date;
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
		return createCellFromXml(context, R.layout.bookmark_item_view, parent);
	}

	@Override
	public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
		super.inflate(r, parser, attrs);

		TypedArray a = r.obtainAttributes(attrs, R.styleable.BookmarkItem);
		link = a.getString(R.styleable.BookmarkItem_link);
		date = a.getString(R.styleable.BookmarkItem_date);
		a.recycle();
	}

}
