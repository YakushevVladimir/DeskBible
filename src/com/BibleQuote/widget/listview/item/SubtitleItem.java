package com.BibleQuote.widget.listview.item;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.BibleQuote.R;
import com.BibleQuote.widget.listview.itemview.ItemView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

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
	 * @param text     The text for this item
	 * @param subtitle The item's subtitle
	 */
	public SubtitleItem(String text, String subtitle) {
		super(text);
		this.subtitletext = subtitle;
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
		return createCellFromXml(context, R.layout.subtitle_item_view, parent);
	}

	@Override
	public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
		super.inflate(r, parser, attrs);

		TypedArray a = r.obtainAttributes(attrs, R.styleable.SubtitleItem);
		subtitletext = a.getString(R.styleable.SubtitleItem_subtitletext);
		a.recycle();
	}
}
