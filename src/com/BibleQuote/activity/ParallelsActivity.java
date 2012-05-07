package com.BibleQuote.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.managers.AsyncCommand;
import com.BibleQuote.managers.AsyncCommand.ICommand;
import com.BibleQuote.managers.AsyncManager;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.OSISLink;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.SubtitleItem;

public class ParallelsActivity extends GDActivity implements OnTaskCompleteListener {

	private Librarian myLibrarian;
	private LinkedHashMap<OSISLink, String> parallels = new LinkedHashMap<OSISLink, String>();
	private String link;
	private AsyncManager mAsyncManager;
	
	private ListView LV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.parallels_list);
        
		BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
		myLibrarian = app.getLibrarian();
		
		LV = (ListView) findViewById(R.id.Parallels_List);
		LV.setOnItemClickListener(list_OnClick);
		
		Intent parent = getIntent();
		link = parent.getStringExtra("link");
		if (link == null) {
			finish();
			return;
		}
		
		String progressMessage = getResources().getString(R.string.messageLoad);
		
		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(getLastNonConfigurationInstance(), this);
		mAsyncManager.setupTask(new AsyncCommand(progressMessage, false, new GetParallesLinks()), this);
	}

	private AdapterView.OnItemClickListener list_OnClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			Toast.makeText(ParallelsActivity.this, R.string.messageSearchCanceled, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onTaskComplete(Task task) {
		if (task != null && !task.isCancelled()) {
			if (task instanceof AsyncCommand) {
				AsyncCommand t = (AsyncCommand) task;
				if (t.isSuccess()) {
					List<Item> items = new ArrayList<Item>();
					for (OSISLink key : parallels.keySet()) {
						items.add(new SubtitleItem(key.toString(), parallels.get(key)));
					}
			        ItemAdapter adapter = new ItemAdapter(this, items);
			        LV.setAdapter(adapter);
				} else {
					// Exception
				}
			}
		}
	}
	
	class GetParallesLinks implements ICommand {
		@Override
		public void execute() {
			parallels = myLibrarian.getParallelsList(link);
		}
	}
}