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

package com.BibleQuote.widget.item;

import com.BibleQuote.widget.itemview.ItemView;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.BibleQuote.R;

public class SubtitleItem extends TextItem {

    /**
     * The subtitle of this item
     */
    public String subtitletext;

    /**
     * @hide
     */
    public SubtitleItem() {
    }

    /**
     * Construct a new SubtitleItem with the specified text and subtitle.
     * 
     * @param text The text for this item
     * @param subtitle The item's subtitle
     */
    public SubtitleItem(String text, String subtitle) {
        super(text);
        this.subtitletext = subtitle;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_subtitle_item_view, parent);
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);

        TypedArray a = r.obtainAttributes(attrs, R.styleable.SubtitleItem);
		subtitletext = a.getString(R.styleable.SubtitleItem_subtitletext);
        a.recycle();
    }
}
