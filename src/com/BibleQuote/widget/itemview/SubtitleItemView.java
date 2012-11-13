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

package com.BibleQuote.widget.itemview;

import com.BibleQuote.widget.item.Item;
import com.BibleQuote.widget.item.SubtitleItem;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.BibleQuote.R;

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
        mTextView = (TextView) findViewById(R.id.bq_text);
        mSubtitleView = (TextView) findViewById(R.id.bq_subtitle);
    }

    public void setObject(Item object) {
        final SubtitleItem item = (SubtitleItem) object;
        mTextView.setText(item.text);
        mSubtitleView.setText(item.subtitletext);
    }

}
