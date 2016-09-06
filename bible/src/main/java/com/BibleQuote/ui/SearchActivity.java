/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: SearchActivity.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.BibleQuote.async.task.command.AsyncCommand;
import com.BibleQuote.async.task.command.StartSearch;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.ExceptionHelper;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.managers.GoogleAnalyticsHelper;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.ui.base.AsyncTaskActivity;
import com.BibleQuote.ui.widget.listview.ItemAdapter;
import com.BibleQuote.ui.widget.listview.item.Item;
import com.BibleQuote.ui.widget.listview.item.SubtextItem;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;
import com.BibleQuote.utils.modules.LinkConverter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class SearchActivity extends AsyncTaskActivity implements TextView.OnEditorActionListener {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private final PreferenceHelper preferenceHelper = PreferenceHelper.getInstance();
    @BindView(R.id.from_book)
    Spinner spinnerFrom;
    @BindView(R.id.to_book)
    Spinner spinnerTo;
    @BindView(R.id.search_list)
    ListView resultList;
    @BindView(R.id.search_text)
    EditText searchText;
    private String progressMessage = "";
	private Map<String, String> searchResults = new LinkedHashMap<String, String>();
    private Librarian myLibrarian;
    private ArrayList<Item> searchItems = new ArrayList<Item>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        myLibrarian = BibleQuoteApp.getInstance().getLibrarian();
        progressMessage = getResources().getString(R.string.messageSearch);

        searchText.setOnEditorActionListener(this);

        String searchModuleID = preferenceHelper.restoreStateString("searchModuleID");
        if (myLibrarian.getModuleID().equalsIgnoreCase(searchModuleID)) {
            searchResults = myLibrarian.getSearchResults();
        }

        if (searchResults.size() == 0) {
            searchText.requestFocus();
        }

        setAdapter();
        spinnerInit();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        spinnerInit();
    }

    @Override
    public void onTaskComplete(Task task) {
        if (task.isCancelled()) {
            Toast.makeText(this, R.string.messageSearchCanceled, Toast.LENGTH_LONG).show();
        } else {
            searchResults = myLibrarian.getSearchResults();
            setAdapter();
        }
        preferenceHelper.saveStateInt("changeSearchPosition", 0);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEARCH:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                startSearch();
                return true;
        }
        return false;
    }

    @OnItemClick(R.id.search_list)
    void openLink(int position) {
        String humanLink = ((SubtextItem) resultList.getAdapter().getItem(position)).text;

        preferenceHelper.saveStateInt("changeSearchPosition", position);

        Intent intent = new Intent();
        intent.putExtra("linkOSIS", LinkConverter.getHumanToOSIS(humanLink));
        setResult(RESULT_OK, intent);

        finish();
    }

    void startSearch() {
        String query = searchText.getText().toString().trim();

        GoogleAnalyticsHelper.getInstance().actionSearch(myLibrarian.getModuleID(), query);

        int posFrom = spinnerFrom.getSelectedItemPosition();
        int posTo = spinnerTo.getSelectedItemPosition();
        if (posFrom == AdapterView.INVALID_POSITION || posTo == AdapterView.INVALID_POSITION) {
            return;
        }
        String fromBookID = ((ItemList) spinnerFrom.getItemAtPosition(posFrom)).get("ID");
        String toBookID = ((ItemList) spinnerTo.getItemAtPosition(posTo)).get("ID");

        Task mTask = new AsyncCommand(new StartSearch(SearchActivity.this, query, fromBookID, toBookID), progressMessage, false);
        mAsyncManager.setupTask(mTask, SearchActivity.this);
    }

    private void restoreSelectedPosition() {
        String searchModuleID = preferenceHelper.restoreStateString("searchModuleID");
        int fromBook = 0;
        int toBook = spinnerTo.getCount() - 1;

        if (myLibrarian.getModuleID().equalsIgnoreCase(searchModuleID)) {
            fromBook = preferenceHelper.restoreStateInt("fromBook");
            if (spinnerFrom.getCount() <= fromBook) {
                fromBook = 0;
            }

            toBook = preferenceHelper.restoreStateInt("toBook");
            if (spinnerTo.getCount() <= toBook) {
                toBook = spinnerTo.getCount() - 1;
            }
		}

        spinnerFrom.setSelection(fromBook);
        spinnerTo.setSelection(toBook);
    }

    private void saveSelectedPosition(int fromBook, int toBook) {
        preferenceHelper.saveStateString("searchModuleID", myLibrarian.getModuleID());
        preferenceHelper.saveStateInt("fromBook", fromBook);
        preferenceHelper.saveStateInt("toBook", toBook);
    }

    /**
     * Устанавливает список результатов поиска по последнему запросу
     */
    private void setAdapter() {
        searchItems.clear();
        for (String key : searchResults.keySet()) {
            String humanLink;
            try {
                humanLink = LinkConverter.getOSIStoHuman(key, myLibrarian);
                searchItems.add(new SubtextItem(humanLink, searchResults.get(key)));
            } catch (BookNotFoundException e) {
                ExceptionHelper.onBookNotFoundException(e, this, TAG);
            } catch (OpenModuleException e) {
                ExceptionHelper.onOpenModuleException(e, this, TAG);
            }
        }
        ItemAdapter adapter = new ItemAdapter(this, searchItems);
        resultList.setAdapter(adapter);

        String searchModuleID = preferenceHelper.restoreStateString("searchModuleID");
        if (myLibrarian.getModuleID().equalsIgnoreCase(searchModuleID)) {
            int changeSearchPosition = preferenceHelper.restoreStateInt("changeSearchPosition");
            if (changeSearchPosition < searchItems.size()) {
                resultList.setSelection(changeSearchPosition);
            }
        }

        String title = getResources().getString(R.string.search);
        if (!searchResults.isEmpty()) {
            title += String.format(Locale.getDefault(),
                    " (%d %s)", searchResults.size(), getResources().getString(R.string.results));
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
        }
    }

    private void spinnerInit() {
        ArrayList<ItemList> books = new ArrayList<ItemList>();
        try {
            books = myLibrarian.getCurrentModuleBooksList();
        } catch (OpenModuleException e) {
            ExceptionHelper.onOpenModuleException(e, this, TAG);
        } catch (BooksDefinitionException e) {
            ExceptionHelper.onBooksDefinitionException(e, this, TAG);
        } catch (BookDefinitionException e) {
            ExceptionHelper.onBookDefinitionException(e, this, TAG);
        }

        SimpleAdapter aa = new SimpleAdapter(this, books,
                android.R.layout.simple_spinner_item,
                new String[]{ItemList.Name}, new int[]{android.R.id.text1});
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                TextView textView = (TextView) view;
                textView.setText(textRepresentation);
                return true;
            }
        };
        aa.setViewBinder(viewBinder);

        spinnerFrom.setAdapter(aa);
        spinnerFrom.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
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
        });

        spinnerTo.setAdapter(aa);
        spinnerTo.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
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
        });

        restoreSelectedPosition();
    }
}
