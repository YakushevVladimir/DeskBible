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

import com.BibleQuote.R;
import com.BibleQuote.BibleQuoteApp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

public class BibleQuote extends GDActivity {

    private static final int START_HOME_ACTIVITY = 10;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setActionBarContentView(R.layout.main);

		getActionBar().setVisibility(View.GONE);

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					BibleQuoteApp app = (BibleQuoteApp) getGDApplication();
					app.Init();
				} finally {
					Message msg = new Message();
					msg.what = START_HOME_ACTIVITY;
					splashHandler.sendMessageDelayed(msg, 100);
					stop();
				}
			}
		};
		splashTread.start();
	}
    
    public void startHomeActivity(){
		startActivity(new Intent(this, Reader.class));
		finish();;
    }

	private Handler splashHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_HOME_ACTIVITY:
				startHomeActivity();
				break;
			}
			super.handleMessage(msg);
		}
	};

/*	
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case R.id.action_bar_view_info:
			//startActivity(new Intent(this, AboutActivity.class));
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
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	}

	public void dbOnBibleOnClick(View v) {
		startActivity(new Intent(this, Reader.class));
	}

	public void dbOnLibraryOnClick(View v) {
        Intent intent = new Intent(jBible.this, Books.class);
        intent.putExtra(Books.GD_ACTION_BAR_TITLE, R.string.db_libraries);
        startActivity(intent);
	}

	public void dbOnBookmarksOnClick(View v) {
		startActivity(new Intent(this, Bookmarks.class));
	}

	public void dbOnNotesOnClick(View v) {
	}

	public void dbOnPlansOnClick(View v) {
	}

	public void dbOnSettingsOnClick(View v) {
		startActivity(new Intent(this, Settings.class));
	}
*/

}