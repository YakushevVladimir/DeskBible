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

import com.BibleQuote.R;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.entity.Librarian;
import com.BibleQuote.utils.AsyncTaskManager;
import com.BibleQuote.utils.FileUtilities;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.Share;
import com.BibleQuote.utils.Task;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Reader extends GDActivity implements OnTaskCompleteListener {

	private static final String TAG = "Reader";
	
	private AsyncTaskManager mAsyncTaskManager;
	private Librarian myLibararian;
    
	private QuickActionWidget webQAction;

	private String chapterInHTML = "";
	private String textColor, textBG;
	private int verse = 1;
	private String selectVerse = "";
	private boolean nightMode = false;
	private String progressMessage = "";
	private int runtimeOrientation;

	private WebView vWeb;
	private LinearLayout vContentBottom;
	private TextView vModuleName;
	private TextView vBookLink;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.reader);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		runtimeOrientation = getScreenOrientation();
		
		Log.i(TAG, "onCreate()");
		
		initActionBar();
		prepareQuickActionBar();

		BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
		myLibararian = app.getLibrarian();
		
		vWeb = (WebView)findViewById(R.id.web);
		vWeb.getSettings().setJavaScriptEnabled(true);
		vWeb.setWebViewClient(webClient);
		vWeb.setVerticalScrollbarOverlay(true);
		
		vContentBottom = (LinearLayout)findViewById(R.id.contentBottom);
		vModuleName = (TextView)findViewById(R.id.moduleName);
		vBookLink = (TextView)findViewById(R.id.linkBook);
		
		mAsyncTaskManager = new AsyncTaskManager(this, this);
		mAsyncTaskManager.handleRetainedTask(getLastNonConfigurationInstance());
		progressMessage = getResources().getString(R.string.messageLoad);

		nightMode = Share.restoreStateBoolean(this, "nightMode");
		
	    String linkOSIS = Share.restoreStateString(this, "last_read");
		if (linkOSIS == null) {
			linkOSIS = myLibararian.getCurrentOSISLink();
			if (linkOSIS == null) {
				return;
			}
		}
		Log.i(TAG, "Open " + linkOSIS);
		mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage), linkOSIS);
	}


	@Override
	protected void onPostResume() {
		Log.i(TAG, "onPostResume()");
		super.onPostResume();
	}
	
	private void initActionBar() {
		Log.i(TAG, "initActionBar()");
		ActionBar bar = getActionBar();
		
		ActionBarItem itemCont = bar.newActionBarItem(NormalActionBarItem.class);
		itemCont.setDrawable(R.drawable.ic_action_bar_content);
		addActionBarItem(itemCont, R.id.action_bar_chooseCh);
		
		ActionBarItem itemPrev = bar.newActionBarItem(NormalActionBarItem.class);
		itemPrev.setDrawable(R.drawable.ic_action_bar_prev);
		addActionBarItem(itemPrev, R.id.action_bar_prevCh);
		
		ActionBarItem itemNext = bar.newActionBarItem(NormalActionBarItem.class);
		itemNext.setDrawable(R.drawable.ic_action_bar_next);
		addActionBarItem(itemNext, R.id.action_bar_nextCh);
	}

    private void prepareQuickActionBar() {
    	webQAction = new QuickActionBar(this);
    	webQAction.addQuickAction(new QuickAction(this, R.drawable.ic_action_bar_bookmark, R.string.fav_add_bookmarks));
    	webQAction.addQuickAction(new QuickAction(this, R.drawable.ic_action_bar_share, R.string.share));
    	webQAction.setOnQuickActionClickListener(mActionListener);
    }

   private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		Log.i(TAG, "onQuickActionClicked(" + widget + ", " + position + ")");
        	switch (position) {
			case 0:
				final String added = getString(R.string.added);
				myLibararian.addBookmark(selectVerse);
				Toast.makeText(getApplicationContext(), added, Toast.LENGTH_LONG).show();
				break;
				
			case 1:
				final String share = getString(R.string.share);
				String shareText = "\""	+ myLibararian.getVerseText(selectVerse) 
					+ "\" (" + myLibararian.getCurrentLink(false) + ":" + selectVerse + ")";
				
				Intent send = new Intent(Intent.ACTION_SEND);
				send.setType("text/plain");
				send.putExtra(Intent.EXTRA_TEXT, shareText);
				startActivity(Intent.createChooser(send, share));;
				break;
				
			case 2:
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ParallelsActivity.class);
				intent.putExtra("linkOSIS", myLibararian.getCurrentOSISLink() + ":" + selectVerse);
				startActivityForResult(intent, R.id.action_bar_chooseCh);
				break;
				
			default:
				break;
			}
        }
    };
    
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		Log.i(TAG, "onHandleActionBarItemClick(" + item + ", " + position + ")");
		switch (item.getItemId()) {
		case R.id.action_bar_prevCh:
			onChPrevClick();
			break;
		case R.id.action_bar_nextCh:
			onChNextClick();
			break;
		case R.id.action_bar_chooseCh:
			onChooseChapterClick();
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, "onConfigurationChanged()");
		if (Share.restoreStateBoolean(this, "DisableAutoScreenRotation")) {
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

	private WebViewClient webClient = new WebViewClient() {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i(TAG, "shouldOverrideUrlLoading(" + url + ")");
			if (!url.contains("verse")) {
				return true;
			}
			selectVerse = url.split(" ")[1];
	       	webQAction.show(vWeb);
			return true;
		}
	};
	
    @Override
	public Object onRetainNonConfigurationInstance() {
		return mAsyncTaskManager.retainTask();
	}

	@Override
	public void onTaskComplete(Task task) {
		// ничего не делаем
	}

	private class ChapterLoader extends Task {
		public ChapterLoader(String message) {
			super(message);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			//runtimeOrientation = getScreenOrientation();
			WebLoadDataWithBaseURL();
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String linkOSIS = params[0];
			String[] linkParam = linkOSIS.split("\\.");
			if (linkParam.length > 3) {
				try {
					verse = Integer.parseInt(linkParam[3]);
				} catch (NumberFormatException e) {
					verse = 1;
				}
			}
			
			chapterInHTML = myLibararian.OpenLink(linkOSIS);
			return true;
		}
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
		Log.i(TAG, "onOptionsItemSelected(" + item.toString() + ")");
		switch (item.getItemId()) {
		case R.id.NightDayMode:
			nightMode = !nightMode;
			Share.saveStateBoolean(this, "nightMode", nightMode);
			mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage),
					myLibararian.getCurrentOSISLink());
			break;
		case R.id.Search:
			Intent intentSearch = new Intent().setClass(
					getApplicationContext(), Search.class);
			startActivityForResult(intentSearch, R.id.action_bar_search);
			break;
		case R.id.Favorites:
			Intent intentBookmarks = new Intent().setClass(
					getApplicationContext(), Bookmarks.class);
			startActivityForResult(intentBookmarks, R.id.action_bar_bookmarks);
			break;
		case R.id.Settings:
			Intent intentSettings = new Intent().setClass(
					getApplicationContext(), Settings.class);
			startActivityForResult(intentSettings, R.id.action_bar_settings);
			break;
		case R.id.About:
			LayoutInflater inflater = getLayoutInflater();
			View about = inflater.inflate(R.layout.about,
					(ViewGroup) findViewById(R.id.about_dialog));

			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.about);
			builder.setIcon(R.drawable.icon);
			builder.setView(about);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			builder.show();
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
				mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage), linkOSIS);
			}
		} else if (requestCode == R.id.action_bar_settings) {
			mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage), myLibararian.getCurrentOSISLink());
		}
	}

	public void WebLoadDataWithBaseURL() {
		Log.i(TAG, "WebLoadDataWithBaseURL()");
		
		if (!nightMode) {
			textColor = myLibararian.getTextColor(this);
			textBG = myLibararian.getTextBackground(this);
		} else {
			textColor = "#ffffff";
			textBG = "#000000";
		}
		String textSize = myLibararian.getTextSize(this);

		String styleDesc = FileUtilities.getAssetString(this,
				myLibararian.isBible() ? "bible_style.css" : "book_style.css");

		String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"> "
				+ "<html>\r\n"
				+ "<head>\r\n"
				+ "<meta http-equiv=Content-Type content=\"text/html; charset=UTF-8\">\r\n"
				+ "<style type=\"text/css\">\r\n"
				+ styleDesc.replaceAll("TextColor", textColor)
						.replaceAll("TextBG", textBG)
						.replaceAll("TextSize", textSize + "pt")
				+ "\r\n"
				+ "</style>\r\n"
				+ "</head>\r\n"
				+ "<body"
				+ "<body onLoad=\"document.location.href='#" + verse + "';\""
				+ ">\r\n"
				+ chapterInHTML
				+ "</body>\r\n" + "</html>";

		vWeb.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
		Share.saveStateString(this, "last_read", myLibararian.getCurrentOSISLink());
		
		vModuleName.setText(myLibararian.getModuleName());
		vBookLink.setText(myLibararian.getHumanBookLink());
		if (!nightMode) {
			vContentBottom.setBackgroundColor(Color.WHITE);
			vModuleName.setTextColor(Color.BLACK);
			vBookLink.setTextColor(Color.BLACK);
		} else {
			vContentBottom.setBackgroundColor(Color.BLACK);
			vModuleName.setTextColor(Color.WHITE);
			vBookLink.setTextColor(Color.WHITE);
		}
	}

	public void onChooseChapterClick() {
		Log.i(TAG, "onChooseChapterClick()");
		Intent intent = new Intent();
		intent.setClass(this, Books.class);
		startActivityForResult(intent, R.id.action_bar_chooseCh);
	}

	public void onChPrevClick() {
		Log.i(TAG, "onChPrevClick()");
		verse = 1;
		myLibararian.prevChapter();
		mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage),
				myLibararian.getCurrentOSISLink());
	}

	public void onChNextClick() {
		Log.i(TAG, "onChNextClick()");
		verse = 1;
		myLibararian.nextChapter();
		mAsyncTaskManager.setupTask(new ChapterLoader(progressMessage),
				myLibararian.getCurrentOSISLink());
	}
}
