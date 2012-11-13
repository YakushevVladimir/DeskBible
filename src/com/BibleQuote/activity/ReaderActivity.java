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

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.controls.ReaderWebView;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.ExceptionHelper;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.listeners.IReaderViewListener;
import com.BibleQuote.managers.AsyncManager;
import com.BibleQuote.managers.AsyncOpenChapter;
import com.BibleQuote.managers.Bookmarks;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.*;
import com.BibleQuote.utils.Share.ShareBuilder.Destination;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


import java.util.TreeSet;

public class ReaderActivity extends SherlockActivity implements OnTaskCompleteListener, IReaderViewListener {

	private static final String TAG = "ReaderActivity";
	private static final int VIEW_CHAPTER_NAV_LENGHT = 3000;

	private static final String VIEW_REFERENCE = "com.BibleQuote.intent.action.VIEW_REFERENCE";

	private Librarian myLibrarian;
	private AsyncManager mAsyncManager;
	private ActionMode currActionMode;

	private String chapterInHTML = "";
	private boolean nightMode = false;
	private String progressMessage = "";

	private TextView vModuleName;
	private TextView vBookLink;
	private LinearLayout btnChapterNav;
	private ReaderWebView vWeb;

	private final class ActionSelectText implements ActionMode.Callback {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater infl = getSupportMenuInflater();
			infl.inflate(R.menu.menu_action_text_select, menu);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			TreeSet<Integer> selVerses = vWeb.getSelectedVerses();
			if (selVerses.size() == 0) {
				return true;
			}

			switch (item.getItemId()) {
			case R.id.action_bookmarks:
				Bookmarks.add(myLibrarian, selVerses.first());
				Toast.makeText(ReaderActivity.this, getString(R.string.added), Toast.LENGTH_LONG).show();
				break;

			case R.id.action_share:
				myLibrarian.shareText(ReaderActivity.this, selVerses, Destination.ActionSend);
				break;

			case R.id.action_copy:
				myLibrarian.shareText(ReaderActivity.this, selVerses, Destination.Clipboard);
				Toast.makeText(ReaderActivity.this, getString(R.string.added), Toast.LENGTH_LONG).show();
				break;

			case R.id.action_references:
				myLibrarian.setCurrentVerseNumber(selVerses.first());
				Intent intParallels = new Intent(VIEW_REFERENCE);
				intParallels.putExtra("linkOSIS", myLibrarian.getCurrentOSISLink().getPath());
				startActivityForResult(intParallels, R.id.action_bar_parallels);
				break;

			default:
				return false;
			}

			mode.finish();
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) {
			vWeb.clearSelectedVerse();
			currActionMode = null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		getSupportActionBar().setIcon(R.drawable.app_logo);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		ViewUtils.setActionBarBackground(this);

		BibleQuoteApp app = (BibleQuoteApp) getApplication();
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

		vModuleName = (TextView)findViewById(R.id.moduleName);
		vBookLink = (TextView)findViewById(R.id.linkBook);

		progressMessage = getResources().getString(R.string.messageLoad);
		nightMode = PreferenceHelper.restoreStateBoolean("nightMode");

		vWeb = (ReaderWebView)findViewById(R.id.readerView);
		vWeb.setOnReaderViewListener(this);
		vWeb.setReadingMode(PreferenceHelper.isReadModeByDefault());
		updateActivityMode();

		BibleReference osisLink = new BibleReference(PreferenceHelper.restoreStateString("last_read"));
		if (!myLibrarian.isOSISLinkValid(osisLink)) {
			onChooseChapterClick();
		} else {
			mAsyncManager.setupTask(new AsyncOpenChapter(progressMessage, false, myLibrarian, osisLink), this);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (PreferenceHelper.restoreStateBoolean("DisableAutoScreenRotation")) {
			super.onConfigurationChanged(newConfig);
			this.setRequestedOrientation(Surface.ROTATION_0);
		} else {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			super.onConfigurationChanged(newConfig);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater infl = getSupportMenuInflater();
		infl.inflate(R.menu.menu_reader, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_bar_chooseCh:
				onChooseChapterClick();
				break;
			case R.id.action_bar_search:
				Intent intentSearch = new Intent().setClass(
						getApplicationContext(), SearchActivity.class);
				startActivityForResult(intentSearch, R.id.action_bar_search);
				break;
			case R.id.action_bar_bookmarks:
				Intent intentBookmarks = new Intent().setClass(
						getApplicationContext(), BookmarksActivity.class);
				startActivityForResult(intentBookmarks, R.id.action_bar_bookmarks);
				break;
			case R.id.NightDayMode:
				nightMode = !nightMode;
				PreferenceHelper.saveStateBoolean("nightMode", nightMode);
				setTextInWebView();
				break;
			case R.id.action_bar_history:
				Intent intentHistory = new Intent().setClass(
						getApplicationContext(), HistoryActivity.class);
				startActivityForResult(intentHistory, R.id.action_bar_history);
				break;
			case R.id.Help:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://scripturesoftware.org/?page_id=427"));
				startActivity(browserIntent);
//				Intent helpIntent = new Intent(this, HelpActivity.class);
//				startActivity(helpIntent);
				break;
			case R.id.Settings:
				Intent intentSettings = new Intent().setClass(
						getApplicationContext(), SettingsActivity.class);
				startActivityForResult(intentSettings, R.id.action_bar_settings);
				break;
			case R.id.About:
				Intent intentAbout = new Intent().setClass(
						getApplicationContext(), AboutActivity.class);
				startActivity(intentAbout);
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
					|| (requestCode == R.id.action_bar_parallels)
					|| (requestCode == R.id.action_bar_history)) {
				Bundle extras = data.getExtras();
				BibleReference osisLink = new BibleReference(extras.getString("linkOSIS"));
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

	public void setTextInWebView() {
		BibleReference OSISLink = myLibrarian.getCurrentOSISLink();
		vWeb.setText(myLibrarian.getBaseUrl(), chapterInHTML, OSISLink.getFromVerse(), nightMode, myLibrarian.isBible());

		PreferenceHelper.saveStateString("last_read", OSISLink.getExtendedPath());

		vModuleName.setText(myLibrarian.getModuleName());
		vBookLink.setText(myLibrarian.getHumanBookLink());

		btnChapterNav.setVisibility(View.GONE);
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
		if (vWeb.isStudyMode()) {
			getSupportActionBar().show();
		} else {
			getSupportActionBar().hide();
		}

		if (!vWeb.isStudyMode()) {
			btnChapterNav.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP && PreferenceHelper.volumeButtonsToScroll())
				|| DevicesKeyCodes.KeyCodeUp(keyCode)) {
			vWeb.pageUp(false);
			viewChapterNav();
			return true;
		} else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && PreferenceHelper.volumeButtonsToScroll())
				|| DevicesKeyCodes.KeyCodeDown(keyCode)) {
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
					setTextInWebView();
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
			TreeSet<Integer> selVerses = vWeb.getSelectedVerses();
			if (selVerses.size() == 0) {
				if (currActionMode != null) {
					currActionMode.finish();
					currActionMode = null;
				}
			} else if (currActionMode == null) {
				currActionMode = startActionMode(new ActionSelectText());
			}
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
