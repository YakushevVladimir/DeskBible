package com.BibleQuote.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.entity.Bible.BibleReference;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.AsyncCommand;
import com.BibleQuote.managers.AsyncCommand.ICommand;
import com.BibleQuote.managers.AsyncManager;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.SubtitleItem;

public class ParallelsActivity extends GDActivity implements OnTaskCompleteListener {

	private static String TAG = "ParallelsActivity";
	
	private Librarian myLibrarian;
	private LinkedHashMap<String, BibleReference> parallels = new LinkedHashMap<String, BibleReference>();
	private BibleReference reference;
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
		String link = parent.getStringExtra("linkOSIS");
		if (link == null) {
			finish();
			return;
		}
		reference = new BibleReference(link);
		
		String progressMessage = getResources().getString(R.string.messageLoad);
		
		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(getLastNonConfigurationInstance(), this);
		mAsyncManager.setupTask(new AsyncCommand(progressMessage, false, new GetParallesLinks()), this);
	}

	private AdapterView.OnItemClickListener list_OnClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			String key = ((SubtitleItem)a.getAdapter().getItem(position)).text;
			BibleReference ref = parallels.get(key);

			Intent intent = new Intent();
			intent.putExtra("linkOSIS", ref.getPath());
			setResult(RESULT_OK, intent);
			finish();
		}
	};

	@Override
	public void onTaskComplete(Task task) {
		if (task != null && !task.isCancelled()) {
			if (task instanceof AsyncCommand) {
				AsyncCommand t = (AsyncCommand) task;
				if (t.isSuccess()) {
					List<Item> items = new ArrayList<Item>();
					for (String link : parallels.keySet()) {
						items.add(new SubtitleItem(link, ""));
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
			try {
				parallels = myLibrarian.getParallelsList(reference);
			} catch (BookNotFoundException e) {
				Log.e(TAG, String.format("Book not found for reference: %1$s", reference));
				parallels = new LinkedHashMap<String, BibleReference>();
			} catch (OpenModuleException e) {
				Log.e(TAG, String.format("Error open module for reference: %1$s", reference));
				parallels = new LinkedHashMap<String, BibleReference>();
			}
		}
	}
}