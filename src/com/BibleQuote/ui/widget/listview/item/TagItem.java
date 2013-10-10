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

package com.BibleQuote.ui.widget.listview.item;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.BibleQuote.R;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.ui.widget.listview.itemview.ItemView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * User: Vladimir Yakushev
 * Date: 10.10.13
 */
public class TagItem extends Item {

	public Tag tag;
	public String name;

	/**
	 * @hide
	 */
	public TagItem() {
	}

	public TagItem(Tag tag) {
		this.tag = tag;
		this.name = tag.name;
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
		return createCellFromXml(context, R.layout.tag_item_view, parent);
	}

	@Override
	public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
		super.inflate(r, parser, attrs);

		TypedArray a = r.obtainAttributes(attrs, R.styleable.TagItem);
		name = a.getString(R.styleable.TagItem_tagName);
		a.recycle();
	}

}
