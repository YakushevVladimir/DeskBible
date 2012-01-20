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

import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.SubtitleItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.exceptions.BookDefinitionException;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.BooksDefinitionException;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.AsyncManager;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.NotifyDialog;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;

public class Search extends GDActivity implements OnTaskCompleteListener {
	private static final String TAG = "Search";
	private Spinner s1, s2;
	private ListView LV;
	private AsyncManager mAsyncManager;
	private String progressMessage = "";

	private LinkedHashMap<String, String> searchResults = new LinkedHashMap<String, String>();
	private Librarian myLibararian;
	private String query = "";
	ArrayList<Item> searchItems = new ArrayList<Item>();
	ArrayList<ItemList> books = new ArrayList<ItemList>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.search);

		BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
		myLibararian = app.getLibrarian();

		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(getLastNonConfigurationInstance(), this);
		
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
			String humanLink;
			try {
				humanLink = myLibararian.getOSIStoHuman(key);
				searchItems.add(new SubtitleItem(humanLink, searchResults.get(key)));
			} catch (BookNotFoundException e) {
				Log.i(TAG, e.toString());
			} catch (OpenModuleException e) {
				Log.i(TAG, e.toString());
			}
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
		books = new ArrayList<ItemList>();
		try {
			books = myLibararian.getCurrentModuleBooksList();
		} catch (OpenModuleException e) {
			// TODO Show an alert with an error message 
			Log.i(TAG, e.toString());
			new NotifyDialog(e.getMessage(), this).show();
		} catch (BooksDefinitionException e) {
			// TODO Show an alert with an error message 
			Log.i(TAG, e.toString());
			new NotifyDialog(e.getMessage(), this).show();
		} catch (BookDefinitionException e) {
			// TODO Show an alert with an error message 
			Log.i(TAG, e.toString());
			new NotifyDialog(e.getMessage(), this).show();
		}

		SimpleAdapter AA = new SimpleAdapter(this, books,
				android.R.layout.simple_spinner_item,
				new String[]{ItemList.Name}, new int[]{android.R.id.text1});;
		AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
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
			String humanLink = ((SubtitleItem) LV.getAdapter()
					.getItem(position)).text;

			Intent intent = new Intent();
			intent.putExtra("linkOSIS", myLibararian.getHumanToOSIS(humanLink));
			setResult(RESULT_OK, intent);

			finish();
		}
	};

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mAsyncManager.retainTask();
	}

	public void onTaskComplete(Task task) {
		if (task.isCancelled()) {
			Toast.makeText(this, R.string.messageSearchCanceled,
					Toast.LENGTH_LONG).show();
		}
	}

	private class StartSearch extends Task {
		public StartSearch(String message, Boolean isHidden) {
			super(message, isHidden);
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

			String fromBookID = ((ItemList) s1.getItemAtPosition(posFrom))
					.get("ID");
			String toBookID = ((ItemList) s2.getItemAtPosition(posTo))
					.get("ID");
			
			searchResults = new LinkedHashMap<String, String>();
			try {
				searchResults = myLibararian.search(query, fromBookID, toBookID);
			} catch (CreateModuleErrorException e) {
				Log.e(TAG, e.getMessage());
			} catch (BookNotFoundException e) {
				Log.e(TAG, e.getMessage());
			} catch (OpenModuleException e) {
				Log.e(TAG, e.getMessage());
			}
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
		mAsyncManager.setupTask(new StartSearch(progressMessage, false), this);
	}

	private OnItemSelectedListener onClick_FromBook = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int fromBook = s1.getSelectedItemPosition();
			int toBook = s2.getSelectedItemPosition();
			if (fromBook > toBook) {
				s2.setSelection(fromBook);
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private OnItemSelectedListener onClick_ToBook = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int fromBook = s1.getSelectedItemPosition();
			int toBook = s2.getSelectedItemPosition();
			if (fromBook > toBook) {
				s1.setSelection(toBook);
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	@Override
	protected void onPostResume() {
		super.onPostResume();
		SpinnerInit();
	}
}
