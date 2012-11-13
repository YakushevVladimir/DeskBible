/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.activity;

import com.BibleQuote.utils.ViewUtils;
import com.actionbarsherlock.app.SherlockActivity;
import com.BibleQuote.widget.ItemAdapter;
import com.BibleQuote.widget.item.Item;
import com.BibleQuote.widget.item.SubtextItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.BibleQuote.exceptions.ExceptionHelper;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.AsyncManager;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends SherlockActivity implements OnTaskCompleteListener {
	private static final String TAG = "SearchActivity";
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
		setContentView(R.layout.search);
		ViewUtils.setActionBarBackground(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BibleQuoteApp app = (BibleQuoteApp) getApplication();
		myLibararian = app.getLibrarian();

		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(getLastNonConfigurationInstance(), this);
		
		progressMessage = getResources().getString(R.string.messageSearch);
		
		String searchModuleID = PreferenceHelper.restoreStateString("searchModuleID");
		if (myLibararian.getModuleID().equalsIgnoreCase(searchModuleID)) {
			searchResults = myLibararian.getSearchResults();
		}

		LV = (ListView) findViewById(R.id.SearchLV);
		LV.setOnItemClickListener(search_OnClick);
		setAdapter();

		SpinnerInit();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;

            default:
                break;
        }
        return true;
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
				Log.i(TAG, e.toString());
			} catch (OpenModuleException e) {
				Log.i(TAG, e.toString());
			}
		}
		ItemAdapter adapter = new ItemAdapter(this, searchItems);
		LV.setAdapter(adapter);

		String searchModuleID = PreferenceHelper.restoreStateString("searchModuleID");
		if (myLibararian.getModuleID().equalsIgnoreCase(searchModuleID)) {
			int changeSearchPosition = PreferenceHelper.restoreStateInt("changeSearchPosition");
			if (changeSearchPosition < searchItems.size()) {
				LV.setSelection(changeSearchPosition);
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
		books = new ArrayList<ItemList>();
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

		s1 = (Spinner) findViewById(R.id.FromBookCB);
		s1.setAdapter(AA);
		s1.setOnItemSelectedListener(onClick_FromBook);

		s2 = (Spinner) findViewById(R.id.ToBookCB);
		s2.setAdapter(AA);
		s2.setOnItemSelectedListener(onClick_ToBook);
		
		restoreSelectedPosition();
	}

	private AdapterView.OnItemClickListener search_OnClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			String humanLink = ((SubtextItem) LV.getAdapter().getItem(position)).text;
			
			PreferenceHelper.saveStateInt("changeSearchPosition", position);
			
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
		PreferenceHelper.saveStateInt("changeSearchPosition", 0);
	}

	private class StartSearch extends Task {
		public StartSearch(String message, Boolean isHidden) {
			super(message, isHidden);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setAdapter();
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
			} catch (BookNotFoundException e) {
				Log.e(TAG, e.getMessage());
			} catch (OpenModuleException e) {
				Log.e(TAG, e.getMessage());
			}
			return true;
		}
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
			int fromBook = s1.getSelectedItemPosition();
			int toBook = s2.getSelectedItemPosition();
			if (fromBook > toBook) {
				s1.setSelection(toBook);
				fromBook = toBook;
			}
			saveSelectedPosition(fromBook, toBook);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	@Override
	protected void onPostResume() {
		super.onPostResume();
		SpinnerInit();
	}

	private void saveSelectedPosition(int fromBook, int toBook) {
		PreferenceHelper.saveStateString("searchModuleID", myLibararian.getModuleID());
		PreferenceHelper.saveStateInt("fromBook", fromBook);
		PreferenceHelper.saveStateInt("toBook", toBook);
	}

	private void restoreSelectedPosition() {
		String searchModuleID = PreferenceHelper.restoreStateString("searchModuleID");
		int fromBook = 0;
		int toBook = s2.getCount() - 1;
		
		if (myLibararian.getModuleID().equalsIgnoreCase(searchModuleID)) {
			fromBook = PreferenceHelper.restoreStateInt("fromBook");
			if (s1.getCount() <= fromBook) {
				fromBook = 0;
			}
			
			toBook = PreferenceHelper.restoreStateInt("toBook");
			if (s2.getCount() <= toBook) {
				toBook = s2.getCount() - 1;
			}
		}
		
		s1.setSelection(fromBook);
		s2.setSelection(toBook);
		
		saveSelectedPosition(fromBook, toBook);
	}
}
