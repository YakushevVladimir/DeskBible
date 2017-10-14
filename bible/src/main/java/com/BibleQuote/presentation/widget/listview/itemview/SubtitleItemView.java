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
 * File: SubtitleItemView.java
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
import com.BibleQuote.presentation.widget.listview.item.Item;
import com.BibleQuote.presentation.widget.listview.item.SubtitleItem;

public class SubtitleItemView extends LinearLayout implements ItemView {

	private TextView mTextView;
	private TextView mSubtitleView;

	public SubtitleItemView(Context context) {
		this(context, null);
	}

	public SubtitleItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void prepareItemView() {
		mTextView = (TextView) findViewById(R.id.text);
		mSubtitleView = (TextView) findViewById(R.id.subtitletext);
	}

	public void setObject(Item object) {
		final SubtitleItem item = (SubtitleItem) object;
		mTextView.setText(item.text);
		mSubtitleView.setText(item.subtitletext);
	}

}
