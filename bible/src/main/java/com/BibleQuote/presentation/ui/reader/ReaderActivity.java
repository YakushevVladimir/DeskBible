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
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.reader;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.BibleQuote.R;
import com.BibleQuote.di.component.ActivityComponent;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.ExceptionHelper;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.textFormatters.ITextFormatter;
import com.BibleQuote.entity.TextAppearance;
import com.BibleQuote.presentation.ui.about.AboutActivity;
import com.BibleQuote.presentation.ui.base.BaseActivity;
import com.BibleQuote.presentation.ui.bookmarks.BookmarksActivity;
import com.BibleQuote.presentation.ui.help.HelpActivity;
import com.BibleQuote.presentation.ui.history.HistoryActivity;
import com.BibleQuote.presentation.ui.imagepreview.ImagePreviewActivity;
import com.BibleQuote.presentation.ui.library.LibraryActivity;
import com.BibleQuote.presentation.ui.reader.tts.TTSPlayerFragment;
import com.BibleQuote.presentation.ui.search.SearchActivity;
import com.BibleQuote.presentation.ui.settings.SettingsActivity;
import com.BibleQuote.presentation.widget.Mode;
import com.BibleQuote.presentation.widget.ReaderWebView;
import com.BibleQuote.utils.DevicesKeyCodes;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.TreeSet;

import javax.inject.Inject;

import ru.churchtools.deskbible.domain.config.FeatureToggle;

public class ReaderActivity extends BaseActivity<ReaderViewPresenter> implements ReaderView, IReaderViewListener {

    public static final int ID_BOOKMARKS = 4;
    public static final int ID_HISTORY = 3;
    public static final int ID_SETTINGS = 6;
    public static final int ID_PARALLELS = 5;
    public static final int ID_CHOOSE_CH = 1;
    public static final int ID_SEARCH = 2;

    @Inject
    FeatureToggle featureToggle;

    private static final String KEY_LINK_OSIS = "linkOSIS";
    private static final String TAG = ReaderActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ReaderWebView readerView;

    private ActionMode currActionMode;
    private boolean exitToBackKey;
    private Mode oldMode;
    private TTSPlayerFragment ttsPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        readerView = findViewById(R.id.readerView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(false);
            drawerLayout.closeDrawers();
            return onNavigationItemSelected(menuItem);
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
        readerView.setOnReaderViewListener(this);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            presenter.openLink(intent.getData());
        } else {
            presenter.openLastLink();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void attachView() {
        presenter.attachView(this);
    }

    @Override
    protected int getRootLayout() {
        return R.layout.activity_reader;
    }

    @Override
    protected void inject(ActivityComponent component) {
        component.inject(this);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideTTSPlayer();
        switch (item.getItemId()) {
            case R.id.action_bar_chooseCh:
                presenter.onClickChooseChapter();
                analyticsHelper.clickEvent("choose_ch");
                break;
            case R.id.action_bar_search:
                openSearchActivity();
                analyticsHelper.clickEvent("search");
                break;
            case R.id.NightDayMode:
                presenter.inverseNightMode();
                analyticsHelper.clickEvent("night_mode");
                break;
            case R.id.action_bar_history:
                openHistoryActivity();
                analyticsHelper.clickEvent("history");
                break;
            case R.id.action_speak:
                viewTTSPlayer();
                analyticsHelper.clickEvent("speak");
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ID_BOOKMARKS:
            case ID_SEARCH:
            case ID_CHOOSE_CH:
            case ID_PARALLELS:
            case ID_HISTORY:
                if (resultCode == RESULT_OK) {
                    presenter.openLink(data.getStringExtra(KEY_LINK_OSIS));
                }
                break;
            case ID_SETTINGS:
                presenter.onChangeSettings();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (exitToBackKey) {
            presenter.onPause();
            super.onBackPressed();
        } else {
            exitToBackKey = true;
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> exitToBackKey = false, 3000);
        }
    }

    @Override
    public boolean onSearchRequested() {
        openSearchActivity();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP && presenter.isVolumeButtonsToScroll())
                || DevicesKeyCodes.keyCodeUp(keyCode)) {
            readerView.pageUp(false);
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && presenter.isVolumeButtonsToScroll())
                || DevicesKeyCodes.keyCodeDown(keyCode)) {
            readerView.pageDown(false);
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
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onReaderViewChange(ChangeCode code) {
        switch (code) {
            case onChangeReaderMode:
                updateActivityMode();
                break;
            case onChangeSelection:
                TreeSet<Integer> selVerses = readerView.getSelectedVerses();
                if (selVerses.size() == 0) {
                    disableActionMode();
                } else if (currActionMode == null) {
                    currActionMode = startSupportActionMode(new SelectTextHandler(this, readerView));
                }
                break;
            case onLongPress:
                if (readerView.getReaderMode() == Mode.Read) {
                    presenter.onClickChooseChapter();
                }
                break;
            case onUpNavigation:
                readerView.pageUp(false);
                break;
            case onDownNavigation:
                readerView.pageDown(false);
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
        openImageViewActivity(path);
    }

    @Override
    public void setCurrentOrientation(boolean disableAutoRotation) {
        if (!disableAutoRotation) {
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
            default:
                // nothing
        }
    }

    @Override
    public void openLibraryActivity() {
        startActivityForResult(LibraryActivity.createIntent(this), ID_CHOOSE_CH);
    }

    @Override
    public void setKeepScreen(boolean isKeepScreen) {
        readerView.setKeepScreenOn(isKeepScreen);
    }

    @Override
    public void setReaderMode(Mode mode) {
        readerView.setMode(mode);
        updateActivityMode();
    }

    @Override
    public void hideTTSPlayer() {
        if (ttsPlayer == null) {
            return;
        }
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.remove(ttsPlayer);
        tran.commit();
        ttsPlayer = null;
        readerView.setMode(oldMode);
    }

    @Override
    public void onOpenChapterFailure(Throwable ex) {
        if (ex instanceof OpenModuleException) {
            ExceptionHelper.onOpenModuleException((OpenModuleException) ex, this, TAG);
        } else if (ex instanceof BookNotFoundException) {
            ExceptionHelper.onBookNotFoundException((BookNotFoundException) ex, this, TAG);
        } else {
            ExceptionHelper.onException(ex, this, TAG);
        }
        hideProgress();
    }

    @Override
    public void viewTTSPlayer() {
        if (ttsPlayer != null) {
            return;
        }

        ttsPlayer = new TTSPlayerFragment();
        ttsPlayer.setTTSStopSpeakListener(presenter);
        getSupportFragmentManager().beginTransaction().add(R.id.tts_player_frame, ttsPlayer).commit();

        oldMode = readerView.getReaderMode();
        readerView.setMode(Mode.Speak);
    }

    @Override
    public void setContent(String baseUrl, Chapter chapter, int verse, boolean isBible) {
        readerView.setContent(baseUrl, chapter, verse, isBible);
    }

    @Override
    public void setTextAppearance(TextAppearance textAppearance) {
        readerView.setTextAppearance(textAppearance);
    }

    @Override
    public void setTitle(String moduleName, String link) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(link);
            actionBar.setSubtitle(moduleName);
        }
    }

    @Override
    public void updateActivityMode() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (readerView.getReaderMode() == Mode.Read) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    @Override
    public void updateContent() {
        readerView.update();
    }

    @Override
    public void setTextFormatter(@NonNull ITextFormatter formatter) {
        readerView.setFormatter(formatter);
    }

    @Override
    public void disableActionMode() {
        if (currActionMode != null) {
            currActionMode.finish();
            currActionMode = null;
        }
    }

    @Override
    public int getCurrVerse() {
        return readerView.getCurrVerse();
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_bookmarks:
                openBookmarkActivity();
                return true;
            case R.id.drawer_tags:
                openTagsActivity();
                return true;
            case R.id.drawer_settings:
                openSettingsActivity();
                return true;
            case R.id.drawer_help:
                openHelpActivity();
                return true;
            case R.id.drawer_about:
                openAboutActivity();
                return true;
            default:
                return false;
        }
    }

    private void openAboutActivity() {
        Intent intentAbout = new Intent().setClass(this, AboutActivity.class);
        startActivity(intentAbout);
    }

    private void openBookmarkActivity() {
        Intent intentBookmarks = new Intent()
                .setClass(this, BookmarksActivity.class)
                .putExtra(BookmarksActivity.EXTRA_MODE, BookmarksActivity.MODE_BOOKMARKS);
        startActivityForResult(intentBookmarks, ID_BOOKMARKS);
    }

    private void openHelpActivity() {
        Intent intentHelp = new Intent(this, HelpActivity.class);
        startActivity(intentHelp);
    }

    private void openHistoryActivity() {
        Intent intentHistory = new Intent().setClass(this, HistoryActivity.class);
        startActivityForResult(intentHistory, ID_HISTORY);
    }

    private void openImageViewActivity(String imagePath) {
        ImagePreviewActivity.IMAGE_PATH = imagePath;
        startActivity(new Intent(this, ImagePreviewActivity.class));
    }

    private void openSearchActivity() {
        Intent intentSearch = new Intent().setClass(this, SearchActivity.class);
        startActivityForResult(intentSearch, ID_SEARCH);
    }

    private void openSettingsActivity() {
        Intent intentSettings = new Intent(this, SettingsActivity.class);
        startActivityForResult(intentSettings, ID_SETTINGS);
    }

    private void openTagsActivity() {
        Intent intentBookmarks = new Intent()
                .setClass(this, BookmarksActivity.class)
                .putExtra(BookmarksActivity.EXTRA_MODE, BookmarksActivity.MODE_TAGS);
        startActivityForResult(intentBookmarks, ID_BOOKMARKS);
    }
}
