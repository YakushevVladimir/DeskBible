package com.BibleQuote.ui.widget.listview.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.BibleQuote.R;
import com.BibleQuote.ui.widget.listview.item.Item;
import com.BibleQuote.ui.widget.listview.item.SubtextItem;

public class SubtextItemView extends LinearLayout implements ItemView {

	private TextView mTextView;
	private TextView mSubtextView;

	public SubtextItemView(Context context) {
		this(context, null);
	}

	public SubtextItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void prepareItemView() {
		mTextView = (TextView) findViewById(R.id.text);
		mSubtextView = (TextView) findViewById(R.id.subtext);
	}

	public void setObject(Item object) {
		final SubtextItem item = (SubtextItem) object;
		mTextView.setText(item.text);
		mSubtextView.setText(item.subtext);
	}

}
