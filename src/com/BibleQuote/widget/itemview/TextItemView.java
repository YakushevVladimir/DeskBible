package com.BibleQuote.widget.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.BibleQuote.widget.item.Item;
import com.BibleQuote.widget.item.TextItem;

public class TextItemView extends TextView implements ItemView {

    public TextItemView(Context context) {
        this(context, null);
    }

    public TextItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void prepareItemView() {
    }

    public void setObject(Item object) {
        setText(((TextItem) object).text);
    }

}
