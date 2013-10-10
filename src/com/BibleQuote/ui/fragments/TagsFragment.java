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

package com.BibleQuote.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.BibleQuote.R;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.managers.tags.TagsManager;
import com.BibleQuote.managers.tags.repository.ITagRepository;
import com.BibleQuote.managers.tags.repository.dbTagRepository;
import com.BibleQuote.ui.widget.listview.ItemAdapter;
import com.BibleQuote.ui.widget.listview.item.Item;
import com.BibleQuote.ui.widget.listview.item.TagItem;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Vladimir Yakushev
 * Date: 14.05.13
 */
public class TagsFragment extends SherlockListFragment implements AdapterView.OnItemLongClickListener {

	private final static String TAG = TagsFragment.class.getSimpleName();
	private final TagsManager tagManager = new TagsManager(new dbTagRepository());

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		setEmptyText(getResources().getText(R.string.empty));

		ListView lw = getListView();
		lw.setLongClickable(true);
		lw.setOnItemLongClickListener(this);

		setAdapter();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.tagSelectListener = (OnTagSelectListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i(TAG, "onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView LV, View v, int position, long id) {
		final Tag currTag = ((TagItem) LV.getAdapter().getItem(position)).tag;
		onTagSelectListenerAlert(currTag);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		final Tag currTag = ((TagItem) adapterView.getItemAtPosition(position)).tag;
		AlertDialog.Builder b = new AlertDialog.Builder(getSherlockActivity());
		b.setIcon(R.drawable.icon);
		b.setTitle(currTag.name);
		b.setMessage(R.string.question_del_tag);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				tagManager.delete(currTag);
			}
		});
		b.setNegativeButton(R.string.cancel, null);
		b.show();
		return true;
	}

	private void setAdapter() {
		List<Item> items = new ArrayList<Item>();
		for (Tag currTag : tagManager.getAll()) {
			items.add(new TagItem(currTag));
		}
		ItemAdapter adapter = new ItemAdapter(getSherlockActivity(), items);
		setListAdapter(adapter);
	}

	public interface OnTagSelectListener {
		void onTagSelect(Tag tag);
	}

	private OnTagSelectListener tagSelectListener;
	public void setOnTagSelectListener(OnTagSelectListener listener) {
		this.tagSelectListener = listener;
	}

	private void onTagSelectListenerAlert(Tag tag) {
		if (this.tagSelectListener != null) {
			this.tagSelectListener.onTagSelect(tag);
		}
	}
}
