/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: BookmarkItemView.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.widget.listview.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.BibleQuote.R;
import com.BibleQuote.presentation.widget.listview.item.BookmarkItem;
import com.BibleQuote.presentation.widget.listview.item.Item;

public class BookmarkItemView extends LinearLayout implements ItemView {

	private TextView mName;
	private TextView mDate;
	private TextView mLink;
	private TextView mTags;

	public BookmarkItemView(Context context) {
		this(context, null);
	}

	public BookmarkItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void prepareItemView() {
		mName = (TextView) findViewById(R.id.name);
		mDate = (TextView) findViewById(R.id.date);
		mLink = (TextView) findViewById(R.id.link);
		mTags = (TextView) findViewById(R.id.tags);
	}

	public void setObject(Item object) {
		final BookmarkItem item = (BookmarkItem) object;
		mName.setText(item.name);
		mDate.setText(item.date);
		mLink.setText(item.link);
		if (!item.tags.equals("")) {
			mTags.setText(item.tags);
		}
	}

}
