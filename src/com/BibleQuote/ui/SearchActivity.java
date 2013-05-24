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

package com.BibleQuote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.AsyncCommand;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.async.command.StartSearch;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.exceptions.*;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;
import com.BibleQuote.utils.ViewUtils;
import com.BibleQuote.ui.widget.listview.ItemAdapter;
import com.BibleQuote.ui.widget.listview.item.Item;
import com.BibleQuote.ui.widget.listview.item.SubtextItem;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SearchActivity extends SherlockFragmentActivity implements OnTaskCompleteListener {
	private static final String TAG = "SearchActivity";
	private Spinner spinnerFrom, spinnerTo;
	private ListView ResultList;
	private AsyncManager mAsyncManager;
	private Task mTask;
	private String progressMessage = "";

	private LinkedHashMap<String, String> searchResults = new LinkedHashMap<String, String>();
	private Librarian myLibararian;
	private ArrayList<Item> searchItems = new ArrayList<Item>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		ViewUtils.setActionBarBackground(this);

		BibleQuoteApp app = (BibleQuoteApp) getApplication();
		myLibararian = app.getLibrarian();

		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(mTask, this);

		progressMessage = getResources().getString(R.string.messageSearch);

		String searchModuleID = PreferenceHelper.restoreStateString("searchModuleID");
		if (myLibararian.getModuleID().equalsIgnoreCase(searchModuleID)) {
			searchResults = myLibararian.getSearchResults();
		}

		((ImageButton) findViewById(R.id.SearchButton)).setOnClickListener(onClick_Search);

		ResultList = (ListView) findViewById(R.id.SearchLV);
		ResultList.setOnItemClickListener(onClick_searchResult);
		setAdapter();

		SpinnerInit();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
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
				searchItems.add(new SubtextItem(humanLink, searchResults.get(key)));
			} catch (BookNotFoundException e) {
				ExceptionHelper.onBookNotFoundException(e, this, TAG);
			} catch (OpenModuleException e) {
				ExceptionHelper.onOpenModuleException(e, this, TAG);
			}
		}
		ItemAdapter adapter = new ItemAdapter(this, searchItems);
		ResultList.setAdapter(adapter);

		String searchModuleID = PreferenceHelper.restoreStateString("searchModuleID");
		if (myLibararian.getModuleID().equalsIgnoreCase(searchModuleID)) {
			int changeSearchPosition = PreferenceHelper.restoreStateInt("changeSearchPosition");
			if (changeSearchPosition < searchItems.size()) {
				ResultList.setSelection(changeSearchPosition);
			}
		}

		String title = getResources().getString(R.string.search);
		if (searchResults.size() > 0) {
			title += " (" + searchResults.size() + " "
					+ getResources().getString(R.string.results) + ")";
		}
		getSupportActionBar().setTitle(title);
	}

	private void SpinnerInit() {
		ArrayList<ItemList> books = new ArrayList<ItemList>();
		try {
			books = myLibararian.getCurrentModuleBooksList();
		} catch (OpenModuleException e) {
			ExceptionHelper.onOpenModuleException(e, this, TAG);
		} catch (BooksDefinitionException e) {
			ExceptionHelper.onBooksDefinitionException(e, this, TAG);
		} catch (BookDefinitionException e) {
			ExceptionHelper.onBookDefinitionException(e, this, TAG);
		}

		SimpleAdapter AA = new SimpleAdapter(this, books,
				android.R.layout.simple_spinner_item,
				new String[]{ItemList.Name}, new int[]{android.R.id.text1});
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

		spinnerFrom = (Spinner) findViewById(R.id.FromBookCB);
		spinnerFrom.setAdapter(AA);
		spinnerFrom.setOnItemSelectedListener(onClick_FromBook);

		spinnerTo = (Spinner) findViewById(R.id.ToBookCB);
		spinnerTo.setAdapter(AA);
		spinnerTo.setOnItemSelectedListener(onClick_ToBook);

		restoreSelectedPosition();
	}

	public void onTaskComplete(Task task) {
		if (task.isCancelled()) {
			Toast.makeText(this, R.string.messageSearchCanceled, Toast.LENGTH_LONG).show();
		} else {
			searchResults = myLibararian.getSearchResults();
			setAdapter();
		}
		PreferenceHelper.saveStateInt("changeSearchPosition", 0);
	}

	private Button.OnClickListener onClick_Search = new Button.OnClickListener() {
		@Override
		public void onClick(View view) {
			String query = ((EditText) findViewById(R.id.SearchEdit)).getText().toString().trim();

			int posFrom = spinnerFrom.getSelectedItemPosition();
			int posTo = spinnerTo.getSelectedItemPosition();
			if (posFrom == AdapterView.INVALID_POSITION || posTo == AdapterView.INVALID_POSITION) {
				return;
			}
			String fromBookID = ((ItemList) spinnerFrom.getItemAtPosition(posFrom)).get("ID");
			String toBookID = ((ItemList) spinnerTo.getItemAtPosition(posTo)).get("ID");

			mTask = new AsyncCommand(new StartSearch(SearchActivity.this, query, fromBookID, toBookID), progressMessage, false);
			mAsyncManager.setupTask(mTask, SearchActivity.this);
		}
	};

	private OnItemClickListener onClick_searchResult = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			String humanLink = ((SubtextItem) ResultList.getAdapter().getItem(position)).text;

			PreferenceHelper.saveStateInt("changeSearchPosition", position);

			Intent intent = new Intent();
			intent.putExtra("linkOSIS", myLibararian.getHumanToOSIS(humanLink));
			setResult(RESULT_OK, intent);

			finish();
		}
	};

	private OnItemSelectedListener onClick_FromBook = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
								   long arg3) {
			int fromBook = spinnerFrom.getSelectedItemPosition();
			int toBook = spinnerTo.getSelectedItemPosition();
			if (fromBook > toBook) {
				spinnerTo.setSelection(fromBook);
				toBook = fromBook;
			}
			saveSelectedPosition(fromBook, toBook);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private OnItemSelectedListener onClick_ToBook = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
								   long arg3) {
			int fromBook = spinnerFrom.getSelectedItemPosition();
			int toBook = spinnerTo.getSelectedItemPosition();
			if (fromBook > toBook) {
				spinnerFrom.setSelection(toBook);
				fromBook = toBook;
			}
			saveSelectedPosition(fromBook, toBook);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private void saveSelectedPosition(int fromBook, int toBook) {
		PreferenceHelper.saveStateString("searchModuleID", myLibararian.getModuleID());
		PreferenceHelper.saveStateInt("fromBook", fromBook);
		PreferenceHelper.saveStateInt("toBook", toBook);
	}

	private void restoreSelectedPosition() {
		String searchModuleID = PreferenceHelper.restoreStateString("searchModuleID");
		int fromBook = 0;
		int toBook = spinnerTo.getCount() - 1;

		if (myLibararian.getModuleID().equalsIgnoreCase(searchModuleID)) {
			fromBook = PreferenceHelper.restoreStateInt("fromBook");
			if (spinnerFrom.getCount() <= fromBook) {
				fromBook = 0;
			}

			toBook = PreferenceHelper.restoreStateInt("toBook");
			if (spinnerTo.getCount() <= toBook) {
				toBook = spinnerTo.getCount() - 1;
			}
		}

		spinnerFrom.setSelection(fromBook);
		spinnerTo.setSelection(toBook);

		saveSelectedPosition(fromBook, toBook);
	}
}
