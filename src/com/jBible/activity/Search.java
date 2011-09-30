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
package com.jBible.activity;

import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.SubtitleItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.jBible.R;
import com.jBible.jBibleApp;
import com.jBible.entity.ItemList;
import com.jBible.entity.Librarian;
import com.jBible.utils.AsyncTaskManager;
import com.jBible.utils.OnTaskCompleteListener;
import com.jBible.utils.Task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

public class Search extends GDActivity implements OnTaskCompleteListener {

	private Spinner s1, s2;
	private ListView LV;
	private AsyncTaskManager mAsyncTaskManager;
	private String progressMessage = "";

	private LinkedHashMap<String, String> searchResults = new LinkedHashMap<String, String>();
	private Librarian myLibararian;
	private String query = "";
	ArrayList<Item> searchItems = new ArrayList<Item>();
	ArrayList<ItemList> books = new ArrayList<ItemList>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.search);

		jBibleApp app = (jBibleApp) getGDApplication();
		myLibararian = app.getLibrarian();
		
		mAsyncTaskManager = new AsyncTaskManager(this, this);
		mAsyncTaskManager.handleRetainedTask(getLastNonConfigurationInstance());
		progressMessage = getResources().getString(R.string.messageSearch);
		searchResults = myLibararian.getSearchResults();

		LV = (ListView) findViewById(R.id.SearchLV);
		LV.setOnItemClickListener(search_OnClick);
		setAdapter();

        SpinnerInit();
	}

	/**
	 * Устанавливает список результатов поиска по последнему запросу
	 */
	private void setAdapter() {
		searchItems.clear();
		for (String key : searchResults.keySet()) {
			searchItems.add(new SubtitleItem(myLibararian.getOSIStoHuman(key), searchResults.get(key)));
		}
        ItemAdapter adapter = new ItemAdapter(this, searchItems);
        LV.setAdapter(adapter);
        
        String title = getResources().getString(R.string.db_search); 
        if (searchResults.size() > 0) {
			title += " (" + searchResults.size() + " "
        	+ getResources().getString(R.string.results) + ")";
		}
        getActionBar().setTitle(title);
	}

	private void SpinnerInit() {
		books = myLibararian.getModuleBooksList();
		
		SimpleAdapter AA = new SimpleAdapter(this, books,
				android.R.layout.simple_spinner_item,
				new String[] { ItemList.Name }, 
				new int[] { android.R.id.text1 });;
		AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {
	        public boolean setViewValue(View view, Object data, String textRepresentation) {
	            TextView textView = (TextView) view;
	            textView.setText(textRepresentation);
	            return true;
	        }
	    };
	    AA.setViewBinder(viewBinder);

		s1 = (Spinner) findViewById(R.id.FromBookCB);
		s1.setAdapter(AA);
		s1.setOnItemSelectedListener(onClick_FromBook);

		s2 = (Spinner) findViewById(R.id.ToBookCB);
		s2.setAdapter(AA);
		s2.setOnItemSelectedListener(onClick_ToBook);
		s2.setSelection(AA.getCount() - 1);
	}

	private AdapterView.OnItemClickListener search_OnClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			String humanLink = ((SubtitleItem)LV.getAdapter().getItem(position)).text;
			
			Intent intent = new Intent();
			intent.putExtra("linkOSIS", myLibararian.getHumanToOSIS(humanLink));
			setResult(RESULT_OK, intent);

			finish();
		}
	};

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mAsyncTaskManager.retainTask();
	}

	@Override
	public void onTaskComplete(Task task) {
		if (task.isCancelled()) {
		    Toast.makeText(this, R.string.messageSearchCanceled, Toast.LENGTH_LONG)
			    .show();
		}
	}

	private class StartSearch extends Task {
		public StartSearch(String message) {
			super(message);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			viewSearchResult();
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			if (query.equals("")) {
				return true;
			}
			
			int posFrom = s1.getSelectedItemPosition();
			int posTo = s2.getSelectedItemPosition();
			if (posFrom == AdapterView.INVALID_POSITION 
					|| posTo == AdapterView.INVALID_POSITION) {
				return true;
			}
			
			String fromBookID = ((ItemList)s1.getItemAtPosition(posFrom)).get("ID");
			String toBookID = ((ItemList)s2.getItemAtPosition(posTo)).get("ID");
			
			searchResults = myLibararian.search(query, fromBookID, toBookID);
			return true;
		}
	}

	public void viewSearchResult() {
		setAdapter();
		myLibararian.setSearchResults(searchResults);
	}

	public void onSearchClick(View v) {
		query = ((EditText) findViewById(R.id.SearchEdit)).getText().toString()
				.trim();
		mAsyncTaskManager.setupTask(new StartSearch(progressMessage));
	}

	private OnItemSelectedListener onClick_FromBook = new OnItemSelectedListener() { 
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			int fromBook = s1.getSelectedItemPosition();
			int toBook = s2.getSelectedItemPosition();
			if (fromBook > toBook) {
				s2.setSelection(fromBook);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private OnItemSelectedListener onClick_ToBook = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			int fromBook = s1.getSelectedItemPosition();
			int toBook = s2.getSelectedItemPosition();
			if (fromBook > toBook) {
				s1.setSelection(toBook);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	@Override
	protected void onPostResume() {
		super.onPostResume();
		SpinnerInit();
	}
}
