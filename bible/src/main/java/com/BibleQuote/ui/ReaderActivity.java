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

package com.BibleQuote.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.async.AsyncOpenChapter;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.ExceptionHelper;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.listeners.IReaderViewListener;
import com.BibleQuote.managers.GoogleAnalyticsHelper;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.modules.Module;
import com.BibleQuote.ui.base.BibleQuoteActivity;
import com.BibleQuote.ui.fragments.TTSPlayerFragment;
import com.BibleQuote.ui.handlers.SelectTextHandler;
import com.BibleQuote.ui.widget.ReaderWebView;
import com.BibleQuote.utils.DevicesKeyCodes;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;

import java.util.TreeSet;

public class ReaderActivity extends BibleQuoteActivity implements OnTaskCompleteListener, IReaderViewListener,
        TTSPlayerFragment.onTTSStopSpeakListener {

    public static final int ID_CHOOSE_CH = 1;
    public static final int ID_SEARCH = 2;
    public static final int ID_HISTORY = 3;
    public static final int ID_BOOKMARKS = 4;
    public static final int ID_PARALLELS = 5;
    public static final int ID_SETTINGS = 6;

    private static final String TAG = "ReaderActivity";

    private ReaderWebView.Mode oldMode;
    private Librarian myLibrarian;
    private AsyncManager mAsyncManager;
    private Task mTask;
    private ActionMode currActionMode;
    private String chapterInHTML = "";
    private boolean nightMode;
    private boolean exitToBackKey;
    private String progressMessage = "";
    private TextView vModuleName;
    private TextView vBookLink;
    private ReaderWebView vWeb;
    private TTSPlayerFragment ttsPlayer;

    @Override
    public void onStopSpeak() {
        hideTTSPlayer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(false);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.drawer_bookmarks:
                        Intent intentBookmarks = new Intent().setClass(ReaderActivity.this, BookmarksActivity.class);
                        startActivityForResult(intentBookmarks, ID_BOOKMARKS);
                        return true;
                    case R.id.drawer_settings:
                        Intent intentSettings = new Intent(ReaderActivity.this, SettingsActivity.class);
                        startActivity(intentSettings);
                        return true;
                    case R.id.drawer_help:
                        Intent intentHelp = new Intent(ReaderActivity.this, HelpActivity.class);
                        startActivity(intentHelp);
                        return true;
                    case R.id.drawer_about:
                        Intent intentAbout = new Intent().setClass(ReaderActivity.this, AboutActivity.class);
                        startActivity(intentAbout);
                        return true;
                    default:
                        return false;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        BibleQuoteApp app = (BibleQuoteApp) getApplication();
        myLibrarian = app.getLibrarian();

        mAsyncManager = app.getAsyncManager();
        mAsyncManager.handleRetainedTask(mTask, this);

        initializeViews();
        setCurrentOrientation();
        updateActivityMode();

        BibleReference osisLink;
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            osisLink = new BibleReference(intent.getData());
        } else {
            osisLink = new BibleReference(PreferenceHelper.restoreStateString("last_read"));
        }

        if (!myLibrarian.isOSISLinkValid(osisLink)) {
            onChooseChapterClick();
        } else {
            openChapterFromLink(osisLink);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideTTSPlayer();
        switch (item.getItemId()) {
            case R.id.action_bar_chooseCh:
                onChooseChapterClick();
                break;
            case R.id.action_bar_search:
                Intent intentSearch = new Intent().setClass(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intentSearch, ID_SEARCH);
                break;
            case R.id.NightDayMode:
                nightMode = !nightMode;
                PreferenceHelper.saveStateBoolean("nightMode", nightMode);
                setTextInWebView();
                break;
            case R.id.action_bar_history:
                Intent intentHistory = new Intent().setClass(getApplicationContext(), HistoryActivity.class);
                startActivityForResult(intentHistory, ID_HISTORY);
                break;
            case R.id.action_speek:
                viewTTSPlayer();
                break;
            default:
                return false;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if ((requestCode == ID_BOOKMARKS)
                    || (requestCode == ID_SEARCH)
                    || (requestCode == ID_CHOOSE_CH)
                    || (requestCode == ID_PARALLELS)
                    || (requestCode == ID_HISTORY)) {
                Bundle extras = data.getExtras();
                BibleReference osisLink = new BibleReference(extras.getString("linkOSIS"));
                if (myLibrarian.isOSISLinkValid(osisLink)) {
                    openChapterFromLink(osisLink);
                    GoogleAnalyticsHelper.getInstance().actionOpenLink(osisLink, requestCode);
                }
            }
        } else if (requestCode == ID_SETTINGS) {
            vWeb.setMode(PreferenceHelper.isReadModeByDefault() ? ReaderWebView.Mode.Read : ReaderWebView.Mode.Study);
            updateActivityMode();
            setCurrentOrientation();
            setKeepScreen();
            openChapterFromLink(myLibrarian.getCurrentOSISLink());
        }
    }

    @Override
    public void onBackPressed() {
        if (exitToBackKey) {
            super.onBackPressed();
        } else {
            exitToBackKey = true;
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitToBackKey = false;
                }
            }, 3000);
        }
    }

    @Override
    public boolean onSearchRequested() {
        Intent intentSearch = new Intent().setClass(
                getApplicationContext(), SearchActivity.class);
        startActivityForResult(intentSearch, ID_SEARCH);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP && PreferenceHelper.volumeButtonsToScroll())
                || DevicesKeyCodes.KeyCodeUp(keyCode)) {
            vWeb.pageUp(false);
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && PreferenceHelper.volumeButtonsToScroll())
                || DevicesKeyCodes.KeyCodeDown(keyCode)) {
            vWeb.pageDown(false);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentOrientation();
        setKeepScreen();
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
                    } else {
                        ExceptionHelper.onException(e, this, TAG);
                    }
                }
            }
        }
    }

    @Override
    public void onReaderViewChange(ChangeCode code) {
        switch (code) {
            case onChangeReaderMode:
                updateActivityMode();
                break;
            case onChangeSelection:
                TreeSet<Integer> selVerses = vWeb.getSelectedVerses();
                if (selVerses.size() == 0) {
                    disableActionMode();
                } else if (currActionMode == null) {
                    currActionMode = startSupportActionMode(new SelectTextHandler(this, vWeb));
                }
                break;
            case onLongPress:
                if (vWeb.getMode() == ReaderWebView.Mode.Read) onChooseChapterClick();
                break;
            case onUpNavigation:
                vWeb.pageUp(false);
                break;
            case onDownNavigation:
                vWeb.pageDown(false);
                break;
            case onLeftNavigation:
                prevChapter();
                break;
            case onRightNavigation:
                nextChapter();
                break;
        }
    }

    public Librarian getLibrarian() {
        return myLibrarian;
    }

    public void setTextInWebView() {
        BibleReference osisLink = myLibrarian.getCurrentOSISLink();
        vWeb.setText(myLibrarian.getBaseUrl(), chapterInHTML, osisLink.getFromVerse(), nightMode, myLibrarian.isBible());

        PreferenceHelper.saveStateString("last_read", osisLink.getExtendedPath());

        vModuleName.setText(myLibrarian.getModuleName());
        vBookLink.setText(myLibrarian.getHumanBookLink());
    }

    public void onChooseChapterClick() {
        Intent intent = new Intent();
        intent.setClass(this, LibraryActivity.class);
        startActivityForResult(intent, ID_CHOOSE_CH);
    }

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

    public void updateActivityMode() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (vWeb.getMode() == ReaderWebView.Mode.Read) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    private void openChapterFromLink(BibleReference osisLink) {
        mTask = new AsyncOpenChapter(progressMessage, false, myLibrarian, osisLink);
        mAsyncManager.setupTask(mTask, this);
    }

    private void initializeViews() {
        vModuleName = (TextView) findViewById(R.id.moduleName);
        vBookLink = (TextView) findViewById(R.id.linkBook);

        progressMessage = getResources().getString(R.string.messageLoad);
        nightMode = PreferenceHelper.restoreStateBoolean("nightMode");

        vWeb = (ReaderWebView) findViewById(R.id.readerView);
        vWeb.setOnReaderViewListener(this);
        vWeb.setMode(PreferenceHelper.isReadModeByDefault() ? ReaderWebView.Mode.Read : ReaderWebView.Mode.Study);

        setKeepScreen();
    }

    private void setCurrentOrientation() {
        if (!PreferenceHelper.restoreStateBoolean("DisableAutoScreenRotation")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            return;
        }

        Display display = getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int height;
        int width;

        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;

        switch (rotation) {
            case Surface.ROTATION_90:
                if (width > height)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_180:
                if (height > width)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Surface.ROTATION_270:
                if (width > height)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default:
                if (height > width)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void setKeepScreen() {
        vWeb.setKeepScreenOn(PreferenceHelper.restoreStateBoolean("DisableTurnScreen"));
    }

    private void viewTTSPlayer() {
        if (ttsPlayer != null) {
            return;
        }

        Module currModule = myLibrarian.getCurrModule();
        if (currModule == null) {
            onChooseChapterClick();
            return;
        }

        ttsPlayer = new TTSPlayerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.tts_player_frame, ttsPlayer).commit();

        oldMode = vWeb.getMode();
        vWeb.setMode(ReaderWebView.Mode.Speak);
    }

    private void hideTTSPlayer() {
        if (ttsPlayer == null) return;
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.remove(ttsPlayer);
        tran.commit();
        ttsPlayer = null;
        vWeb.setMode(oldMode);
    }

    private void viewCurrentChapter() {
        disableActionMode();
        openChapterFromLink(myLibrarian.getCurrentOSISLink());
    }

    private void disableActionMode() {
        if (currActionMode != null) {
            currActionMode.finish();
            currActionMode = null;
        }
    }
}
