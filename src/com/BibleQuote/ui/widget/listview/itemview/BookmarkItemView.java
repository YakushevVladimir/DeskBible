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

package com.BibleQuote.ui.widget.listview.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.BibleQuote.R;
import com.BibleQuote.ui.widget.listview.item.BookmarkItem;
import com.BibleQuote.ui.widget.listview.item.Item;

public class BookmarkItemView extends LinearLayout implements ItemView {

	private TextView mLink;
	private TextView mDate;
	private TextView mTags;

	public BookmarkItemView(Context context) {
		this(context, null);
	}

	public BookmarkItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void prepareItemView() {
		mLink = (TextView) findViewById(R.id.link);
		mDate = (TextView) findViewById(R.id.date);
		mTags = (TextView) findViewById(R.id.tags);
	}

	public void setObject(Item object) {
		final BookmarkItem item = (BookmarkItem) object;
		mLink.setText(item.link);
		mDate.setText(item.date);
		mTags.setText(getContext().getText(R.string.no_tags));
	}

}
