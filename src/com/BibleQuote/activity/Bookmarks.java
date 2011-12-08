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
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.entity.Librarian;
import com.BibleQuote.utils.Log;

public class Bookmarks extends GDActivity {

	private final String TAG = "Bookmarks";
	
    private QuickActionWidget mGrid;
    
    private ListView LV;
	private Librarian myLibararian;
	private String currBookmark;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.favorits);
		
		initActionBar();
		prepareQuickActionBar();

		BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
		myLibararian = app.getLibrarian();
		
		LV = (ListView) findViewById(R.id.FavoritsLV);
		LV.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, myLibararian.getBookmarks()));
		LV.setOnItemClickListener(OnItemClickListener);
		LV.setOnItemLongClickListener(OnItemLongClickListener);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		LV.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, myLibararian.getBookmarks()));
	}

    private void prepareQuickActionBar() {
    	mGrid = new QuickActionGrid(this);
    	mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_action_bar_sort, R.string.fav_sort_alphabetically));
    	mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_action_bar_delete, R.string.fav_delete_all));
    	mGrid.setOnQuickActionClickListener(mActionListener);
    }

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
        	switch (position) {
			case 0:
				myLibararian.sortBookmarks();
				setAdapter();
				break;

			case 1:
				Builder builder = new AlertDialog.Builder(Bookmarks.this);
				builder.setIcon(R.drawable.icon);
				builder.setTitle(R.string.favorites);
				builder.setMessage(R.string.fav_delete_all_question);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						myLibararian.delAllBookmarks();
						setAdapter();
					}
				});
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});
				builder.show();
				break;

			default:
				break;
			}
        }
    };
    
	private void initActionBar() {
		ActionBarItem itemNext = getActionBar().newActionBarItem(NormalActionBarItem.class);
		itemNext.setDrawable(R.drawable.ic_menu_more);
		addActionBarItem(itemNext, R.id.action_bar_more);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case R.id.action_bar_more:
        	if (LV.getAdapter().getCount() == 0) {
				return true;
			}
			mGrid.show(getActionBar().getItem(0).getItemView());
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	}

	private AdapterView.OnItemClickListener OnItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			String link = LV.getAdapter().getItem(position).toString();
			String linkOSIS = myLibararian.getBookmark(link);
			
			Log.i(TAG, "Select bookmark: " + link + " (OSIS link = " + linkOSIS + ")");

			Intent intent = new Intent();
			intent.putExtra("linkOSIS", linkOSIS);
			setResult(RESULT_OK, intent);

			finish();
		}
	};

	private AdapterView.OnItemLongClickListener OnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
			currBookmark = LV.getAdapter().getItem(position).toString();
			Builder b = new AlertDialog.Builder(Bookmarks.this);
			b.setIcon(R.drawable.icon);
			b.setTitle(R.string.favorites);
			b.setMessage(R.string.fav_question_del_fav);
			b.setPositiveButton("OK", positiveButton_OnClick);
			b.setNegativeButton(R.string.cancel, null);
			b.show();
			return true;
		}
	};

	private DialogInterface.OnClickListener positiveButton_OnClick = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			Log.i(TAG, "Delete bookmark: " + currBookmark);
			
			myLibararian.delBookmark(currBookmark);
			setAdapter();
			Toast.makeText(getApplicationContext(), R.string.removed,
					Toast.LENGTH_LONG).show();
		}
	};
	
	private void setAdapter() {
		LV.setAdapter(new ArrayAdapter<String>(Bookmarks.this,
				android.R.layout.simple_list_item_1, myLibararian.getBookmarks()));
	}
    
	private static class MyQuickAction extends QuickAction {
        
        private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);

        public MyQuickAction(Context ctx, int drawableId, int titleId) {
            super(ctx, buildDrawable(ctx, drawableId), titleId);
        }
        
        private static Drawable buildDrawable(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            d.setColorFilter(BLACK_CF);
            return d;
        }
        
    }
}
