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
import android.util.Log;
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

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.controls.ReaderWebView;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.ExceptionHelper;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.listeners.IReaderViewListener;
import com.BibleQuote.managers.AsyncManager;
import com.BibleQuote.managers.AsyncOpenChapter;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.OSISLink;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;

public class ReaderActivity extends GDActivity implements OnTaskCompleteListener, IReaderViewListener {

	private static final String TAG = "ReaderActivity";
	private static final int VIEW_CHAPTER_NAV_LENGHT = 3000;
	
	private Librarian myLibrarian;
	private AsyncManager mAsyncManager;
    
	private QuickActionWidget textQAction;

	private String chapterInHTML = "";
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
		
		initActionBar();
		prepareQuickActionBar();
		
		BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
		myLibrarian = app.getLibrarian();

		mAsyncManager = app.getAsyncManager();
		mAsyncManager.handleRetainedTask(getLastNonConfigurationInstance(), this);
		
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
		vWeb.setOnReaderViewListener(this);
		vWeb.setReadingMode(PreferenceHelper.isReadModeByDefault());
		updateActivityMode();
		
		OSISLink osisLink = new OSISLink(PreferenceHelper.restoreStateString("last_read"));
		if (!myLibrarian.isOSISLinkValid(osisLink)) {
			onChooseChapterClick();
		} else {
			mAsyncManager.setupTask(new AsyncOpenChapter(progressMessage, false, myLibrarian, osisLink), this);
		}
	}
	
	private void initActionBar() {
		ActionBar bar = getActionBar();
		
		ActionBarItem itemCont = bar.newActionBarItem(NormalActionBarItem.class);
		itemCont.setDrawable(R.drawable.ic_action_bar_content);
		addActionBarItem(itemCont, R.id.action_bar_chooseCh);
		
		ActionBarItem itemSearch = bar.newActionBarItem(NormalActionBarItem.class);
		itemSearch.setDrawable(R.drawable.ic_action_bar_search);
		addActionBarItem(itemSearch, R.id.action_bar_search);
		
		ActionBarItem itemHistory = bar.newActionBarItem(NormalActionBarItem.class);
		itemHistory.setDrawable(R.drawable.ic_action_bar_recent_history);
		addActionBarItem(itemHistory, R.id.action_bar_history);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case R.id.action_bar_chooseCh:
			onChooseChapterClick();
			break;
		case R.id.action_bar_search:
			Intent intentSearch = new Intent().setClass(
					getApplicationContext(), SearchActivity.class);
			startActivityForResult(intentSearch, R.id.action_bar_search);
			break;
		case R.id.action_bar_history:
			Intent intentHistory = new Intent().setClass(
					getApplicationContext(), HistoryActivity.class);
			startActivityForResult(intentHistory, R.id.action_bar_history);
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
		public void onClick(View btnTextAction) {
	    	textQAction.show(btnTextAction);
		}
	};

   private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		ReaderWebView wView = (ReaderWebView)findViewById(R.id.readerView);
			TreeSet<Integer> selVerses = wView.getSelectedVerses();
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
		MenuInflater infl = getMenuInflater();
		infl.inflate(R.menu.menu_reader, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.NightDayMode:
			nightMode = !nightMode;
			PreferenceHelper.saveStateBoolean("nightMode", nightMode);
			setTextinWebView();
			break;
		case R.id.Favorites:
			Intent intentBookmarks = new Intent().setClass(
					getApplicationContext(), 
					BookmarksActivity.class);
			startActivityForResult(intentBookmarks, R.id.action_bar_bookmarks);
			break;
		case R.id.Help:
			Intent helpIntent = new Intent(this, HelpActivity.class);
			startActivity(helpIntent);
			break;
		case R.id.Settings:
			Intent intentSettings = new Intent().setClass(
					getApplicationContext(), 
					SettingsActivity.class);
			startActivityForResult(intentSettings, R.id.action_bar_settings);
			break;
		case R.id.About:
			Intent intentAbout = new Intent().setClass(
					getApplicationContext(), 
					AboutActivity.class);
			startActivity(intentAbout);
			break;
		case R.id.Search:
			Intent intentSearch = new Intent().setClass(
					getApplicationContext(), SearchActivity.class);
					startActivityForResult(intentSearch, R.id.action_bar_search);
			break;
		default:
			return false;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if ((requestCode == R.id.action_bar_bookmarks) 
					|| (requestCode == R.id.action_bar_search )
					|| (requestCode == R.id.action_bar_chooseCh)
					|| (requestCode == R.id.action_bar_history)) {
				Bundle extras = data.getExtras();
				OSISLink osisLink = new OSISLink(extras.getString("linkOSIS"));
				if (myLibrarian.isOSISLinkValid(osisLink)) {
					mAsyncManager.setupTask(new AsyncOpenChapter(progressMessage, false, myLibrarian, osisLink), this);
				}
			}
		} else if (requestCode == R.id.action_bar_settings) {
			vWeb.setReadingMode(PreferenceHelper.isReadModeByDefault());
			updateActivityMode();
			mAsyncManager.setupTask(new AsyncOpenChapter(progressMessage, false, myLibrarian, myLibrarian.getCurrentOSISLink()), this);
		}
	}

	public void setTextinWebView() {
		OSISLink OSISLink = myLibrarian.getCurrentOSISLink();
		vWeb.setText(myLibrarian.getBaseUrl(), chapterInHTML, OSISLink.getVerseNumber(), nightMode, myLibrarian.isBible());
		
		PreferenceHelper.saveStateString("last_read", OSISLink.getExtendedPath());
		
		vModuleName.setText(myLibrarian.getModuleName());
		vBookLink.setText(myLibrarian.getHumanBookLink());
		
		btnChapterNav.setVisibility(View.GONE);
		btnTextAction.setVisibility(View.GONE);
	}

	public void onChooseChapterClick() {
		Intent intent = new Intent();
		intent.setClass(this, LibraryActivity.class);
		startActivityForResult(intent, R.id.action_bar_chooseCh);
	}

	OnClickListener onClickChapterPrev = new OnClickListener() {
		public void onClick(View v) {
			prevChapter();
		}
	};
	
	OnClickListener onClickChapterNext = new OnClickListener() {
		public void onClick(View v) {
			nextChapter();
		}
	};

	public void prevChapter() {
		try {
			myLibrarian.prevChapter();
		} catch (OpenModuleException e) {
			Log.e(TAG, "prevChapter()", e);
		}
		viewCurrentChapter();
	}

	public void nextChapter() {
		try {
			myLibrarian.nextChapter();
		} catch (OpenModuleException e) {
			Log.e(TAG, "nextChapter()", e);
		}
		viewCurrentChapter();
	}

	OnClickListener onClickPageUp = new OnClickListener() {
		public void onClick(View v) {
			vWeb.pageUp(false);
			viewChapterNav();
		}
	};

	OnClickListener onClickPageDown = new OnClickListener() {
		public void onClick(View v) {
			vWeb.pageDown(false);
			viewChapterNav();
		}
	};

	private void viewCurrentChapter() {
		mAsyncManager.setupTask(new AsyncOpenChapter(
				progressMessage, false, myLibrarian, myLibrarian.getCurrentOSISLink()), this);
	}
	
	public void setTextActionVisibility() {
		if (!vWeb.isStudyMode() 
				|| vWeb.getSelectedVerses().size() == 0) {
			btnTextAction.setVisibility(View.GONE);
		} else {
			btnTextAction.setVisibility(View.VISIBLE);
		}
	}

	public void viewChapterNav() {
		if (chapterNavHandler.hasMessages(R.id.view_chapter_nav)) {
			chapterNavHandler.removeMessages(R.id.view_chapter_nav);
		}
		if (!vWeb.isStudyMode()) {
			btnChapterNav.setVisibility(View.GONE);
		} else {
			btnChapterNav.setVisibility(View.VISIBLE);
			if (!vWeb.isScrollToBottom()) {
				Message msg = new Message();
				msg.what = R.id.view_chapter_nav;
				chapterNavHandler.sendMessageDelayed(msg, VIEW_CHAPTER_NAV_LENGHT);
			}
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
		getApplicationContext(), SearchActivity.class);
		startActivityForResult(intentSearch, R.id.action_bar_search);
		return false;
	}

	public void updateActivityMode() {
		int visible = vWeb.isStudyMode() ? View.VISIBLE : View.GONE;
		getActionBar().setVisibility(visible);
		if (!vWeb.isStudyMode()) {
			btnChapterNav.setVisibility(View.GONE);
			btnTextAction.setVisibility(View.GONE);
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
    	return mAsyncManager.retainTask();
    }
    
    public void onTaskComplete(Task task) {
		if (task != null && !task.isCancelled()) {
			if (task instanceof AsyncOpenChapter) {
				AsyncOpenChapter t = ((AsyncOpenChapter) task);
				if (t.isSuccess()) {
					chapterInHTML = myLibrarian.getChapterHTMLView();
					setTextinWebView();
					// TODO Sergey: open the next chapter in background
					//mAsyncManager.setupTask(new AsyncOpenChapter(progressMessage, false, myLibrarian, OSISLink), this);
				} else {
					Exception e = t.getException();
					if (e instanceof OpenModuleException) {
						ExceptionHelper.onOpenModuleException((OpenModuleException) e, this, TAG);
					} else if (e instanceof BookNotFoundException) {
						ExceptionHelper.onBookNotFoundException((BookNotFoundException) e, this, TAG);
					}  
				}
			}
		}
    }
    
	@Override
	public void onReaderViewChange(ChangeCode code) {
		if (code == ChangeCode.onChangeReaderMode) {
			updateActivityMode();
		} else if (code == ChangeCode.onUpdateText
				|| code == ChangeCode.onScroll) {
			viewChapterNav();
		} else if (code == ChangeCode.onChangeSelection) {
			viewChapterNav();
			setTextActionVisibility();
		} else if (code == ChangeCode.onLongPress) {
			if (vWeb.isStudyMode()) {
				viewChapterNav();
			} else {
				onChooseChapterClick();
			}
		} else if (code == ChangeCode.onUpNavigation) {
			vWeb.pageUp(false);
		} else if (code == ChangeCode.onDownNavigation) {
			vWeb.pageDown(false);
		} else if (code == ChangeCode.onLeftNavigation) {
			prevChapter();
		} else if (code == ChangeCode.onRightNavigation) {
			nextChapter();
		}
	}

}
