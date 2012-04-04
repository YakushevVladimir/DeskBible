package com.BibleQuote.activity;

import java.util.LinkedList;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.managers.Librarian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

public class HistoryActivity extends GDActivity {

	//private final String TAG = "HistoryActivity";
	
    private ListView vHistoryList;
	private Librarian myLibrarian;
	private LinkedList<ItemList> list = new LinkedList<ItemList>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.favorits);
		initActionBar();
        
		BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
		myLibrarian = app.getLibrarian();
		
		setListAdapter();
		vHistoryList.setOnItemClickListener(OnItemClickListener);
    }

	private void setListAdapter() {
		list = myLibrarian.getHistoryList(); 
		vHistoryList = (ListView) findViewById(R.id.FavoritsLV);
		vHistoryList.setAdapter(new SimpleAdapter(this, list,
				R.layout.item_list_no_id,
				new String[] { ItemList.ID, ItemList.Name }, new int[] {
						R.id.id, R.id.name }));
	}
	
	private AdapterView.OnItemClickListener OnItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			Intent intent = new Intent();
			intent.putExtra("linkOSIS", list.get(position).get(ItemList.ID));
			setResult(RESULT_OK, intent);
			finish();
		}
	};

	private void initActionBar() {
		ActionBar bar = getActionBar();
		
		ActionBarItem itemHistory = bar.newActionBarItem(NormalActionBarItem.class);
		itemHistory.setDrawable(R.drawable.gd_action_bar_trashcan);
		addActionBarItem(itemHistory, R.id.action_bar_history_clear);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case R.id.action_bar_history_clear:
			myLibrarian.clearHistory();
			setListAdapter();
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	}
	
}
