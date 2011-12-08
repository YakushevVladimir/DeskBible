package com.BibleQuote.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import greendroid.app.GDListActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;

public class ParallelsActivity extends GDListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		List<Item> items = new ArrayList<Item>();
		items.add(new TextItem("Ge 1:3"));
		items.add(new TextItem("Ge 1:3"));
		items.add(new TextItem("Ge 1:3"));
		items.add(new TextItem("Ge 1:3"));
		items.add(new TextItem("Ge 1:3"));

        ItemAdapter adapter = new ItemAdapter(this, items);
        setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(this, ((TextItem) l.getAdapter().getItem(position)).text, Toast.LENGTH_LONG).show();
	}
}
