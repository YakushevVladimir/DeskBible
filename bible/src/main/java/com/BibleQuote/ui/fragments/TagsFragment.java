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
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.BibleQuote.R;
import com.BibleQuote.managers.tags.Tag;
import com.BibleQuote.managers.tags.TagsManager;
import com.BibleQuote.managers.tags.repository.dbTagRepository;
import com.BibleQuote.ui.widget.listview.ItemAdapter;
import com.BibleQuote.ui.widget.listview.item.Item;
import com.BibleQuote.ui.widget.listview.item.TagItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * User: Vladimir Yakushev
 * Date: 14.05.13
 */
public class TagsFragment extends ListFragment implements AdapterView.OnItemLongClickListener {

    private final static String TAG = TagsFragment.class.getSimpleName();
    private final TagsManager tagManager = new TagsManager(new dbTagRepository());
    private OnTagsChangeListener onTagsChangeListener;

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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            setOnTagSelectListener((OnTagsChangeListener) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_tags, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_refresh:
                setAdapter();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        final Tag currTag = ((TagItem) lv.getAdapter().getItem(position)).tag;
        onTagSelectListenerAlert(currTag);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        final Tag currTag = ((TagItem) adapterView.getItemAtPosition(position)).tag;
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setIcon(R.drawable.icon);
        b.setTitle(currTag.name);
        b.setMessage(R.string.question_del_tag);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tagManager.delete(currTag);
                setAdapter();
                onTagsUppdateListenerAlert();
            }
        });
        b.setNegativeButton(R.string.cancel, null);
        b.show();
        return true;
    }

    private void setAdapter() {
        List<Item> items = new ArrayList<Item>();
        LinkedHashMap<Tag, String> tagList = tagManager.getAllWithCount();
        for (Tag currTag : tagList.keySet()) {
            items.add(new TagItem(currTag, tagList.get(currTag)));
        }
        ItemAdapter adapter = new ItemAdapter(getActivity(), items);
        setListAdapter(adapter);
    }

    public void updateTags() {
        setAdapter();
    }

    public void setOnTagSelectListener(OnTagsChangeListener listener) {
        this.onTagsChangeListener = listener;
    }

    private void onTagSelectListenerAlert(Tag tag) {
        if (this.onTagsChangeListener != null) {
            this.onTagsChangeListener.onTagSelect(tag);
        }
    }

    private void onTagsUppdateListenerAlert() {
        if (this.onTagsChangeListener != null) {
            this.onTagsChangeListener.onTagsUpdate();
        }
    }

    public interface OnTagsChangeListener {
        void onTagSelect(Tag tag);

        void onTagsUpdate();
    }
}
