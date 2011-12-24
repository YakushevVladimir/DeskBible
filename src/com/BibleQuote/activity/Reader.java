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
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.TreeSet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BibleQuote.R;
import com.BibleQuote.TempBibleQuoteApp;
import com.BibleQuote.controls.ReaderWebView;
import com.BibleQuote.entity.Librarian;
import com.BibleQuote.utils.AsyncTaskManager;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;

public class Reader extends GDActivity implements OnTaskCompleteListener {

	private static final String TAG = "Reader";
	private static final int VIEW_CHAPTER_NAV_LENGHT = 5000;
	
	private Librarian myLibrarian;
    private AsyncTaskManager mAsyncTaskManager;
    
	private QuickActionWidget textQAction;

	private String chapterInHTML = "";
	private int verse = 1;
	private boolean nightMode = false;
	private String progressMessage = "";
	private int runtimeOrientation;

	private TextView vModuleName;
	private TextView vBookLink;
	private ImageButton btnTextAction;
	private LinearLayout btnChapterNav;
	private ReaderWebView vWeb;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.reader);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		runtimeOrientation = getScreenOrientation();
		
		Log.i(TAG, "onCreate()");
		
		initActionBar();
		prepareQuickActionBar();
		
		mAsyncTaskManager = new AsyncTaskManager(this, this, false);
		mAsyncTaskManager.handleRetainedTask(getLastNonConfigurationInstance());

		TempBibleQuoteApp app = (TempBibleQuoteApp) getGDApplication();
		myLibrarian = app.getLibrarian();
		
		btnChapterNav = (LinearLayout)findViewById(R.id.btn_chapter_nav);
		
		ImageButton btnChapterPrev = (ImageButton)findViewById(R.id.btn_reader_prev);
		btnChapterPrev.setOnClickListener(onClickChapterPrev);
		ImageButton btnChapterNext = (ImageButton)findViewById(R.id.btn_reader_next);
		btnChapterNext.setOnClickListener(onClickChapterNext);
		
		ImageButton btnChapterUp = (ImageButton)findViewById(R.id.btn_reader_up);
		btnChapterUp.setOnClickListener(onClickPageUp);
		ImageButton btnChapterDown = (ImageButton)findViewById(R.id.btn_reader_down);
		btnChapterDown.setOnClickListener(onClickPageDown);
		
		btnTextAction = (ImageButton)findViewById(R.id.btn_text_action);
		btnTextAction.setOnClickListener(onClickBtnTextAction);
		
		vModuleName = (TextView)findViewById(R.id.moduleName);
		vBookLink = (TextView)findViewById(R.id.linkBook);
		
		progressMessage = getResources().getString(R.string.messageLoad);
		nightMode = PreferenceHelper.restoreStateBoolean("nightMode");
		
		vWeb = (ReaderWebView)findViewById(R.id.readerView);
		vWeb.setReadingMode(PreferenceHelper.isReadModeByDefault());
		updateActivityMode();
		
	    String linkOSIS = PreferenceHelper.restoreStateString("last_read");
		if (linkOSIS == null) {
			linkOSIS = myLibrarian.getCurrentOSISLink();
			if (linkOSIS == null) {
				return;
			}
		}
		Log.i(TAG, "Open " + linkOSIS);
		
		mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage), linkOSIS);
	}
	
	private void initActionBar() {
		Log.i(TAG, "initActionBar()");
		ActionBar bar = getActionBar();
		
		ActionBarItem itemCont = bar.newActionBarItem(NormalActionBarItem.class);
		itemCont.setDrawable(R.drawable.ic_action_bar_content);
		addActionBarItem(itemCont, R.id.action_bar_chooseCh);
		
		ActionBarItem itemSearch = bar.newActionBarItem(NormalActionBarItem.class);
		itemSearch.setDrawable(R.drawable.ic_action_bar_search);
		addActionBarItem(itemSearch, R.id.action_bar_search);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		Log.i(TAG, "onHandleActionBarItemClick(" + item + ", " + position + ")");
		switch (item.getItemId()) {
		case R.id.action_bar_chooseCh:
			onChooseChapterClick();
			break;
		case R.id.action_bar_search:
			Intent intentSearch = new Intent().setClass(
					getApplicationContext(), Search.class);
			startActivityForResult(intentSearch, R.id.action_bar_search);
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	}
	
   private void prepareQuickActionBar() {
    	textQAction = new QuickActionBar(this);
    	textQAction.addQuickAction(new QuickAction(this, R.drawable.ic_action_bar_bookmark, R.string.fav_add_bookmarks));
    	textQAction.addQuickAction(new QuickAction(this, R.drawable.ic_action_bar_share, R.string.share));
    	textQAction.addQuickAction(new QuickAction(this, R.drawable.ic_action_bar_clipboard, R.string.copy));
    	textQAction.setOnQuickActionClickListener(mActionListener);
    }
    
    OnClickListener onClickBtnTextAction = new OnClickListener() {
		@Override
		public void onClick(View btnTextAction) {
	    	textQAction.show(btnTextAction);
		}
	};

   private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		Log.i(TAG, "onQuickActionClicked(" + widget + ", " + position + ")");
			
    		ReaderWebView wView = (ReaderWebView)findViewById(R.id.readerView);
			TreeSet<Integer> selVerses = wView.getSelectVerses();
			if (selVerses.size() == 0) {
				return;
			}
        	
			switch (position) {
			case 0:
				final String added = getString(R.string.added);
				
				myLibrarian.addBookmark(selVerses.first());
				Toast.makeText(getApplicationContext(), added, Toast.LENGTH_LONG).show();
				break;
				
			case 1:
				String shareText = myLibrarian.getShareText(selVerses);
				
				final String share = getString(R.string.share);
				Intent send = new Intent(Intent.ACTION_SEND);
				send.setType("text/plain");
				send.putExtra(Intent.EXTRA_TEXT, shareText);
				startActivity(Intent.createChooser(send, share));;
				break;
			
			case 2:
				ClipboardManager clpbdManager = (ClipboardManager)getSystemService("clipboard");
			    if (clpbdManager != null) {
					String clpbdText = myLibrarian.getShareText(selVerses);
					clpbdManager.setText(clpbdText);
			    }
			    
			default:
				break;
			}
        }
    };
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, "onConfigurationChanged()");
		if (PreferenceHelper.restoreStateBoolean("DisableAutoScreenRotation")) {
			super.onConfigurationChanged(newConfig);
			this.setRequestedOrientation(runtimeOrientation);
		} else {
			runtimeOrientation = this.getScreenOrientation();
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			super.onConfigurationChanged(newConfig);
		}
	}

	protected int getScreenOrientation() {
		Log.i(TAG, "getScreenOrientation()");
		Display display = getWindowManager().getDefaultDisplay();
		int orientation = display.getOrientation();

		if (orientation == Configuration.ORIENTATION_UNDEFINED) {
			orientation = getResources().getConfiguration().orientation;

			if (orientation == Configuration.ORIENTATION_UNDEFINED) {
				if (display.getWidth() == display.getHeight())
					orientation = Configuration.ORIENTATION_SQUARE;
				else if (display.getWidth() < display.getHeight())

					orientation = Configuration.ORIENTATION_PORTRAIT;
				else
					orientation = Configuration.ORIENTATION_LANDSCAPE;
			}
		}
		return orientation;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "onCreateOptionsMenu()");
		MenuInflater infl = getMenuInflater();
		infl.inflate(R.menu.menu_reader, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "onOptionsItemSelected()");
		switch (item.getItemId()) {
		case R.id.NightDayMode:
			nightMode = !nightMode;
			PreferenceHelper.saveStateBoolean("nightMode", nightMode);
			setTextinWebView();
			break;
		case R.id.Favorites:
			Intent intentBookmarks = new Intent().setClass(
					getApplicationContext(), 
					Bookmarks.class);
			startActivityForResult(intentBookmarks, R.id.action_bar_bookmarks);
			break;
		case R.id.Help:
			Intent helpIntent = new Intent(this, HelpActivity.class);
			startActivity(helpIntent);
			break;
		case R.id.Settings:
			Intent intentSettings = new Intent().setClass(
					getApplicationContext(), 
					Settings.class);
			startActivityForResult(intentSettings, R.id.action_bar_settings);
			break;
		case R.id.About:
			Intent intentAbout = new Intent().setClass(
					getApplicationContext(), 
					AboutActivity.class);
			startActivity(intentAbout);
			break;
		default:
			return false;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if ((requestCode == R.id.action_bar_bookmarks) 
					|| (requestCode == R.id.action_bar_search )
					|| (requestCode == R.id.action_bar_chooseCh)) {
				verse = 1;
				Bundle extras = data.getExtras();
				String linkOSIS = extras.getString("linkOSIS");
				
				AsyncTaskManager mAsyncTaskManager = new AsyncTaskManager(this, this, false);
				mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage), linkOSIS);
			}
		} else if (requestCode == R.id.action_bar_settings) {
			vWeb.setReadingMode(PreferenceHelper.isReadModeByDefault());
			updateActivityMode();
			AsyncTaskManager mAsyncTaskManager = new AsyncTaskManager(this, this, false);
			mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage), myLibrarian.getCurrentOSISLink());
		}
	}

	public void setTextinWebView() {
		Log.i(TAG, "WebLoadDataWithBaseURL()");
		
		vWeb.setText(chapterInHTML, verse, nightMode, myLibrarian.isBible());
		
		PreferenceHelper.saveStateString("last_read", myLibrarian.getCurrentOSISLink());
		
		vModuleName.setText(myLibrarian.getModuleName());
		vBookLink.setText(myLibrarian.getHumanBookLink());
		
		btnChapterNav.setVisibility(View.GONE);
		btnTextAction.setVisibility(View.GONE);
	}

	public void onChooseChapterClick() {
		Log.i(TAG, "onChooseChapterClick()");
		Intent intent = new Intent();
		intent.setClass(this, Books.class);
		startActivityForResult(intent, R.id.action_bar_chooseCh);
	}

	OnClickListener onClickChapterPrev = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClickChapterPrev()");
			prevChapter();
		}
	};
	
	OnClickListener onClickChapterNext = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClickChapterPrev()");
			nextChapter();
		}
	};

	public void prevChapter() {
		myLibrarian.prevChapter();
		viewNewChapter();
	}

	public void nextChapter() {
		myLibrarian.nextChapter();
		viewNewChapter();
	}

	OnClickListener onClickPageUp = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClickPageUp()");
			vWeb.pageUp(false);
			viewChapterNav();
		}
	};

	OnClickListener onClickPageDown = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClickPageUp()");
			vWeb.pageDown(false);
			viewChapterNav();
		}
	};

	private void viewNewChapter() {
		verse = 1;
		AsyncTaskManager mAsyncTaskManager = new AsyncTaskManager(this, this, false);
		mAsyncTaskManager.setupTask(
				new ChapterLoader(progressMessage),
				myLibrarian.getCurrentOSISLink());
	}
	
	public void setTextActionVisibility(boolean visibility) {
		if (visibility) {
			btnTextAction.setVisibility(View.VISIBLE);
		} else {
			btnTextAction.setVisibility(View.GONE);
		}
	}

	public void viewChapterNav() {
		if (chapterNavHandler.hasMessages(R.id.view_chapter_nav)) {
			chapterNavHandler.removeMessages(R.id.view_chapter_nav);
		}
		btnChapterNav.setVisibility(View.VISIBLE);
		
		ReaderWebView vWeb = (ReaderWebView)findViewById(R.id.readerView);
		if (!vWeb.isScrollToBottom()) {
			Message msg = new Message();
			msg.what = R.id.view_chapter_nav;
			chapterNavHandler.sendMessageDelayed(msg, VIEW_CHAPTER_NAV_LENGHT);
		}
	}
	
	private Handler chapterNavHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.view_chapter_nav:
				btnChapterNav.setVisibility(View.GONE);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public boolean onSearchRequested() {
		Intent intentSearch = new Intent().setClass(
		getApplicationContext(), Search.class);
		startActivityForResult(intentSearch, R.id.action_bar_search);
		return false;
	}

	public void updateActivityMode() {
		getActionBar().setVisibility(vWeb.isStudyMode() ? View.VISIBLE : View.GONE);
		if (!vWeb.isStudyMode() && btnChapterNav.getVisibility() == View.VISIBLE) {
			btnChapterNav.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			vWeb.pageUp(false);
			viewChapterNav();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			vWeb.pageDown(false);
			viewChapterNav();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
    @Override
    public Object onRetainNonConfigurationInstance() {
    	return mAsyncTaskManager.retainTask();
    }

    @Override
    public void onTaskComplete(Task task) {
		Log.i(TAG, "onTaskComplete()");
		if (!task.isCancelled()) {
			setTextinWebView();
		}
    }
    
	private class ChapterLoader extends Task {
		public ChapterLoader(String message) {
			super(message);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Log.i(TAG, "doInBackground(\"" + params + "\")");
			try {
				String linkOSIS = params[0];
				String[] linkParam = linkOSIS.split("\\.");
				if (linkParam.length > 3) {
					try {
						verse = Integer.parseInt(linkParam[3]);
					} catch (NumberFormatException e) {
						verse = 1;
					}
				}
				
				chapterInHTML = myLibrarian.OpenLink(linkOSIS);
			} catch (NullPointerException e) {
				chapterInHTML = "";
				Log.e(TAG, e);
			}
			return true;
		}
	}

}
