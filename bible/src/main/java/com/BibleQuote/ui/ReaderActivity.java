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
 * File: ReaderActivity.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
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
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.listeners.IReaderViewListener;
import com.BibleQuote.ui.base.BibleQuoteActivity;
import com.BibleQuote.ui.fragments.TTSPlayerFragment;
import com.BibleQuote.ui.handlers.SelectTextHandler;
import com.BibleQuote.ui.presenters.ReaderViewPresenter;
import com.BibleQuote.ui.widget.ReaderWebView;
import com.BibleQuote.utils.DevicesKeyCodes;

import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReaderActivity extends BibleQuoteActivity implements ReaderViewPresenter.IReaderView, IReaderViewListener {

    @BindView(R.id.moduleName)
    TextView vModuleName;
    @BindView(R.id.linkBook)
    TextView vBookLink;
    @BindView(R.id.readerView)
    ReaderWebView vWeb;

    private ReaderWebView.Mode oldMode;
    private ActionMode currActionMode;
    private boolean nightMode;
    private boolean exitToBackKey;
    private TTSPlayerFragment ttsPlayer;
    private ReaderViewPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final DrawerLayout drawerLayout = ButterKnife.findById(this, R.id.drawer_layout);
        final NavigationView navigationView = ButterKnife.findById(this, R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(false);
                drawerLayout.closeDrawers();
                return presenter.onNavigationItemSelected(menuItem.getItemId());
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
        actionBarDrawerToggle.syncState();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        vWeb.setOnReaderViewListener(this);

        BibleReference osisLink = null;
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            osisLink = new BibleReference(intent.getData());
        }

        presenter = new ReaderViewPresenter(this, this, BibleQuoteApp.getInstance().getLibrarian());
        presenter.setOSISLink(osisLink);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item.getItemId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
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
        openSearchActivity(ReaderViewPresenter.ID_SEARCH);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP && presenter.isVolumeButtonsToScroll())
                || DevicesKeyCodes.KeyCodeUp(keyCode)) {
            vWeb.pageUp(false);
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && presenter.isVolumeButtonsToScroll())
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
        presenter.onResume();
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
                if (vWeb.getMode() == ReaderWebView.Mode.Read) {
                    openLibraryActivity(ReaderViewPresenter.ID_CHOOSE_CH);
                }
                break;
            case onUpNavigation:
                vWeb.pageUp(false);
                break;
            case onDownNavigation:
                vWeb.pageDown(false);
                break;
            case onLeftNavigation:
                presenter.prevChapter();
                break;
            case onRightNavigation:
                presenter.nextChapter();
                break;
        }
    }

    @Override
    public void onReaderClickImage(String path) {
        presenter.onClickImage(path);
    }

    @Override
    public void setCurrentOrientation(boolean disableAutoRotation) {
        if (disableAutoRotation) {
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
                if (width > height) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
                break;
            case Surface.ROTATION_180:
                if (height > width) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;
            case Surface.ROTATION_270:
                if (width > height) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case Surface.ROTATION_0:
                if (height > width) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
        }
    }

    @Override
    public void openBookmarkActivity(int requestCode) {
        Intent intentBookmarks = new Intent()
                .setClass(this, BookmarksActivity.class)
                .putExtra(BookmarksActivity.EXTRA_MODE, BookmarksActivity.MODE_BOOKMARKS);
        startActivityForResult(intentBookmarks, requestCode);
    }

    @Override
    public void openAboutActivity() {
        Intent intentAbout = new Intent().setClass(this, AboutActivity.class);
        startActivity(intentAbout);
    }

    @Override
    public void openHelpActivity() {
        Intent intentHelp = new Intent(this, HelpActivity.class);
        startActivity(intentHelp);
    }

    @Override
    public void openImageViewActivity(String imagePath) {
        Intent intent = new Intent(this, ImageViewActivity.class);
        intent.putExtra(ImageViewActivity.EXTRA_IMAGE_PATH, imagePath);
        startActivity(intent);
    }

    @Override
    public void openLibraryActivity(int requestCode) {
        Intent intent = new Intent().setClass(this, LibraryActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void openSettingsActivity(int requestCode) {
        Intent intentSettings = new Intent(this, SettingsActivity.class);
        startActivityForResult(intentSettings, requestCode);
    }

    @Override
    public void openTagsActivity(int requestCode) {
        Intent intentBookmarks = new Intent()
                .setClass(this, BookmarksActivity.class)
                .putExtra(BookmarksActivity.EXTRA_MODE, BookmarksActivity.MODE_TAGS);
        startActivityForResult(intentBookmarks, requestCode);
    }

    @Override
    public void openHistoryActivity(int requestCode) {
        Intent intentHistory = new Intent().setClass(this, HistoryActivity.class);
        startActivityForResult(intentHistory, requestCode);
    }

    @Override
    public void openSearchActivity(int requestCode) {
        Intent intentSearch = new Intent().setClass(this, SearchActivity.class);
        startActivityForResult(intentSearch, requestCode);
    }

    @Override
    public void setKeepScreen(boolean isKeepScreen) {
        vWeb.setKeepScreenOn(isKeepScreen);
    }

    @Override
    public void setNightMode(boolean isNightMode) {
        nightMode = isNightMode;
        vWeb.setNightMode(nightMode);
    }

    @Override
    public boolean invertNightMode() {
        nightMode = !nightMode;
        vWeb.setNightMode(nightMode);
        return nightMode;
    }

    @Override
    public void setReaderMode(ReaderWebView.Mode mode) {
        vWeb.setMode(mode);
        updateActivityMode();
    }

    @Override
    public void hideTTSPlayer() {
        if (ttsPlayer == null) return;
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.remove(ttsPlayer);
        tran.commit();
        ttsPlayer = null;
        vWeb.setMode(oldMode);
    }

    @Override
    public void viewTTSPlayer() {
        if (ttsPlayer != null) {
            return;
        }

        ttsPlayer = new TTSPlayerFragment();
        ttsPlayer.setTTSStopSpeakListener(presenter);
        getSupportFragmentManager().beginTransaction().add(R.id.tts_player_frame, ttsPlayer).commit();

        oldMode = vWeb.getMode();
        vWeb.setMode(ReaderWebView.Mode.Speak);
    }

    @Override
    public void setContent(String baseUrl, Chapter chapter, int verse, boolean isBible) {
        vWeb.setContent(baseUrl, chapter, verse, nightMode, isBible);
    }

    @Override
    public void setTitle(String moduleName, String link) {
        vModuleName.setText(moduleName);
        vBookLink.setText(link);
    }

    @Override
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

    @Override
    public void updateContent() {
        vWeb.update();
    }

    @Override
    public void disableActionMode() {
        if (currActionMode != null) {
            currActionMode.finish();
            currActionMode = null;
        }
    }
}
