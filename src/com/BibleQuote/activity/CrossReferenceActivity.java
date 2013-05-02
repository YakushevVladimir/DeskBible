/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.AsyncCommand;
import com.BibleQuote.async.AsyncCommand.ICommand;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.ExceptionHelper;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;
import com.BibleQuote.utils.ViewUtils;
import com.BibleQuote.widget.listview.ItemAdapter;
import com.BibleQuote.widget.listview.item.Item;
import com.BibleQuote.widget.listview.item.SubtextItem;
import com.BibleQuote.widget.listview.item.TextItem;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CrossReferenceActivity extends SherlockFragmentActivity implements OnTaskCompleteListener {

	private static String TAG = "CrossReferenceActivity";

	private Librarian myLibrarian;
	private LinkedHashMap<String, BibleReference> crossReference = new LinkedHashMap<String, BibleReference>();
	private HashMap<BibleReference, String> crossReferenceContent = new HashMap<BibleReference, String>();
	private BibleReference bReference;
	private AsyncManager mAsyncManager;
	private Task mTask;

	private ListView LV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parallels_list);
		ViewUtils.setActionBarBackground(this);

		BibleQuoteApp app = (BibleQuoteApp) getApplication();
		myLibrarian = app.getLibrarian();

		LV = (ListView) findViewById(R.id.Parallels_List);
		LV.setOnItemClickListener(list_OnClick);

		Intent parent = getIntent();
		String link = parent.getStringExtra("linkOSIS");
		if (link == null) {
			finish();
			return;
		}
		bReference = new BibleReference(link);

		String bookName;
		try {
			bookName = myLibrarian.getBookFullName(bReference.getModuleID(), bReference.getBookID());
		} catch (OpenModuleException e) {
			bookName = bReference.getBookFullName();
		}

		TextView referenceSource = (TextView) findViewById(R.id.referenceSource);
		referenceSource.setText(String.format("%1$s %2$s:%3$s", bookName, bReference.getChapter(), bReference.getFromVerse()));

		String progressMessage = getResources().getString(R.string.messageLoad);

		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(mTask, this);
		if (mTask == null) {
			mTask = new AsyncCommand(new GetParallesLinks(), progressMessage, false);
			mAsyncManager.setupTask(mTask, this);
		}
	}

	private AdapterView.OnItemClickListener list_OnClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			String key = ((TextItem) a.getAdapter().getItem(position)).text;
			BibleReference ref = crossReference.get(key);

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
					setListAdapter();
				} else {
					Exception e = t.getException();
					ExceptionHelper.onException(e, this, TAG);
				}
			}
		}
	}

	private void setListAdapter() {
		List<Item> items = new ArrayList<Item>();
		for (String link : crossReference.keySet()) {
			if (PreferenceHelper.crossRefViewDetails()) {
				items.add(new SubtextItem(link, crossReferenceContent.get(crossReference.get(link))));
			} else {
				items.add(new TextItem(link));
			}
		}

		ItemAdapter adapter = new ItemAdapter(this, items);
		LV.setAdapter(adapter);
	}

	class GetParallesLinks implements ICommand {
		@Override
		public void execute() throws Exception {
			crossReference = myLibrarian.getCrossReference(bReference);
			if (PreferenceHelper.crossRefViewDetails()) {
				crossReferenceContent = myLibrarian.getCrossReferenceContent(crossReference.values());
			}
		}
	}
}