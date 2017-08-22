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
 * File: CrossReferenceActivity.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.task.command.AsyncCommand;
import com.BibleQuote.async.task.command.AsyncCommand.ICommand;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.exceptions.ExceptionHelper;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.ui.base.AsyncTaskActivity;
import com.BibleQuote.ui.widget.listview.ItemAdapter;
import com.BibleQuote.ui.widget.listview.item.Item;
import com.BibleQuote.ui.widget.listview.item.SubtextItem;
import com.BibleQuote.ui.widget.listview.item.TextItem;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrossReferenceActivity extends AsyncTaskActivity {

    private static final String TAG = CrossReferenceActivity.class.getSimpleName();
	private ListView LV;
    private BibleReference bReference;
    private LinkedHashMap<String, BibleReference> crossReference = new LinkedHashMap<>();
    private HashMap<BibleReference, String> crossReferenceContent = new HashMap<>();
	private AdapterView.OnItemClickListener list_OnClick = (a, v, position, id) -> {
		String key = ((TextItem) a.getAdapter().getItem(position)).text;
		BibleReference ref = crossReference.get(key);

		Intent intent = new Intent();
		intent.putExtra("linkOSIS", ref.getPath());
		setResult(RESULT_OK, intent);
		finish();
	};
	private Librarian myLibrarian;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parallels_list);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mAsyncManager.isWorking()) {
            String progressMessage = getString(R.string.messageLoad);
            mAsyncManager.setupTask(new AsyncCommand(new GetParallelsLinks(), progressMessage, false), this);
        }
    }

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
        List<Item> items = new ArrayList<>();
        PreferenceHelper prefHelper = BibleQuoteApp.getInstance().getPrefHelper();
		for (Map.Entry<String, BibleReference> entry : crossReference.entrySet()) {
			if (prefHelper.crossRefViewDetails()) {
				items.add(new SubtextItem(entry.getKey(), crossReferenceContent.get(entry.getValue())));
			} else {
				items.add(new TextItem(entry.getKey()));
			}
		}

		ItemAdapter adapter = new ItemAdapter(this, items);
		LV.setAdapter(adapter);
	}

    private class GetParallelsLinks implements ICommand {

        @Override
        public boolean execute() throws Exception {
            crossReference = myLibrarian.getCrossReference(bReference);
            PreferenceHelper prefHelper = BibleQuoteApp.getInstance().getPrefHelper();
            if (prefHelper.crossRefViewDetails()) {
                crossReferenceContent = myLibrarian.getCrossReferenceContent(crossReference.values());
			}
            return true;
        }
	}
}