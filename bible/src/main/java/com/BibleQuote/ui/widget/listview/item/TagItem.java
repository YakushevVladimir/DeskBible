/*
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
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: TagItem.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.ui.widget.listview.item;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.BibleQuote.R;
import com.BibleQuote.domain.entity.Tag;
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
	public String count;

	/**
	 *
	 */
	public TagItem() {
	}

	public TagItem(Tag tag, String count) {
		this.tag = tag;
		this.name = tag.name;
		this.count = count;
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
		count = a.getString(R.styleable.TagItem_tagCount);
		a.recycle();
	}

}
