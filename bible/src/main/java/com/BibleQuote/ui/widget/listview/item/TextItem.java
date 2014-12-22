package com.BibleQuote.ui.widget.listview.item;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.BibleQuote.R;
import com.BibleQuote.ui.widget.listview.itemview.ItemView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class TextItem extends Item {

	/**
	 * The item's text.
	 */
	public String text;

	/**
	 * @hide
	 */
	public TextItem() {
	}

	/**
	 * Create a new TextItem with the specified text.
	 *
	 * @param text The text used to create this item.
	 */
	public TextItem(String text) {
		this.text = text;
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
		return createCellFromXml(context, R.layout.text_item_view, parent);
	}

	@Override
	public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
		super.inflate(r, parser, attrs);

		TypedArray a = r.obtainAttributes(attrs, R.styleable.TextItem);
		text = a.getString(R.styleable.TextItem_text);
		a.recycle();
	}

}
