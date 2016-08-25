/*
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
 * File: ReaderViewPresenter.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 *
 */

package com.BibleQuote.ui.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.async.AsyncOpenChapter;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.ExceptionHelper;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.managers.GoogleAnalyticsHelper;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.ui.fragments.TTSPlayerFragment;
import com.BibleQuote.ui.widget.ReaderWebView;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnTaskCompleteListener;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;

import java.lang.ref.WeakReference;

/**
 *
 */
public class ReaderViewPresenter implements OnTaskCompleteListener, TTSPlayerFragment.OnTTSStopSpeakListener, IViewPresenter {

    private static final String TAG = ReaderViewPresenter.class.getSimpleName();

    public static final int ID_CHOOSE_CH = 1;
    public static final int ID_SEARCH = 2;
    public static final int ID_HISTORY = 3;
    public static final int ID_BOOKMARKS = 4;
    public static final int ID_PARALLELS = 5;
    public static final int ID_SETTINGS = 6;

    private String progressMessage;
    private IReaderView view;
    private WeakReference<Context> weakContext;
    private Librarian librarian;
    private Task mTask;
    private AsyncManager asyncManager;

    public ReaderViewPresenter(Context context, IReaderView view, Librarian librarian) {
        this.weakContext = new WeakReference<Context>(context);
        this.librarian = librarian;
        this.progressMessage = context.getString(R.string.messageLoad);
        this.asyncManager = BibleQuoteApp.getInstance().getAsyncManager();
        this.asyncManager.handleRetainedTask(mTask, this);
        this.view = view;

        initView();
    }

    @Override
    public void onStopSpeak() {
        view.hideTTSPlayer();
    }

    @Override
    public void onTaskComplete(Task task) {
        Context context = weakContext.get();
        if (context == null) {
            return;
        }

        if (task != null && !task.isCancelled()) {
            if (task instanceof AsyncOpenChapter) {
                AsyncOpenChapter chapterTask = ((AsyncOpenChapter) task);
                if (chapterTask.isSuccess()) {
                    BibleReference osisLink = librarian.getCurrentOSISLink();
                    view.setContent(librarian.getBaseUrl(), librarian.getChapterHTMLView(), osisLink.getFromVerse(), librarian.isBible());
                    view.setTitle(librarian.getModuleName(), librarian.getHumanBookLink());
                    PreferenceHelper.saveStateString("last_read", osisLink.getExtendedPath());
                } else {
                    Exception e = chapterTask.getException();
                    if (e instanceof OpenModuleException) {
                        ExceptionHelper.onOpenModuleException((OpenModuleException) e, context, TAG);
                    } else if (e instanceof BookNotFoundException) {
                        ExceptionHelper.onBookNotFoundException((BookNotFoundException) e, context, TAG);
                    } else {
                        ExceptionHelper.onException(e, context, TAG);
                    }
                }
            }
        }
    }

    @Override
    public Context getContext() {
        return weakContext.get();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if ((requestCode == ID_BOOKMARKS)
                    || (requestCode == ID_SEARCH)
                    || (requestCode == ID_CHOOSE_CH)
                    || (requestCode == ID_PARALLELS)
                    || (requestCode == ID_HISTORY)) {
                Bundle extras = data.getExtras();
                BibleReference osisLink = new BibleReference(extras.getString("linkOSIS"));
                if (librarian.isOSISLinkValid(osisLink)) {
                    openChapterFromLink(osisLink);
                    GoogleAnalyticsHelper.getInstance().actionOpenLink(osisLink, requestCode);
                }
            }
        } else if (requestCode == ID_SETTINGS) {
            initView();
            view.updateContent();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        view.setCurrentOrientation(PreferenceHelper.restoreStateBoolean("DisableAutoScreenRotation"));
    }

    @Override
    public boolean onNavigationItemSelected(int itemId) {
        switch (itemId) {
            case R.id.drawer_bookmarks:
                view.openBookmarkActivity(ID_BOOKMARKS);
                return true;
            case R.id.drawer_tags:
                view.openTagsActivity(ID_BOOKMARKS);
                return true;
            case R.id.drawer_settings:
                view.openSettingsActivity(ID_SETTINGS);
                return true;
            case R.id.drawer_help:
                view.openHelpActivity();
                return true;
            case R.id.drawer_about:
                view.openAboutActivity();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(int itemId) {
        view.hideTTSPlayer();
        switch (itemId) {
            case R.id.action_bar_chooseCh:
                view.openLibraryActivity(ID_CHOOSE_CH);
                break;
            case R.id.action_bar_search:
                view.openSearchActivity(ID_SEARCH);
                break;
            case R.id.NightDayMode:
                PreferenceHelper.saveStateBoolean("nightMode", view.invertNightMode());
                view.updateContent();
                break;
            case R.id.action_bar_history:
                view.openHistoryActivity(ID_HISTORY);
                break;
            case R.id.action_speek:
                view.viewTTSPlayer();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        view.setKeepScreen(PreferenceHelper.restoreStateBoolean("DisableTurnScreen"));
        view.setCurrentOrientation(PreferenceHelper.restoreStateBoolean("DisableAutoScreenRotation"));
    }

    @Override
    public void onPause() {

    }

    public boolean isVolumeButtonsToScroll() {
        return PreferenceHelper.volumeButtonsToScroll();
    }

    public void nextChapter() {
        try {
            librarian.nextChapter();
        } catch (OpenModuleException e) {
            Log.e(TAG, "nextChapter()", e);
        }
        viewCurrentChapter();
    }

    public void prevChapter() {
        try {
            librarian.prevChapter();
        } catch (OpenModuleException e) {
            Log.e(TAG, "prevChapter()", e);
        }
        viewCurrentChapter();
    }

    public void setOSISLink(BibleReference osisLink) {
        if (osisLink == null) {
            osisLink = new BibleReference(PreferenceHelper.restoreStateString("last_read"));
        }

        if (!librarian.isOSISLinkValid(osisLink)) {
            view.openLibraryActivity(ID_CHOOSE_CH);
        } else {
            openChapterFromLink(osisLink);
        }
    }

    private void initView() {
        view.setNightMode(PreferenceHelper.restoreStateBoolean("nightMode"));
        view.setReaderMode(PreferenceHelper.isReadModeByDefault() ? ReaderWebView.Mode.Read : ReaderWebView.Mode.Study);
        view.setKeepScreen(PreferenceHelper.restoreStateBoolean("DisableTurnScreen"));
        view.setCurrentOrientation(PreferenceHelper.restoreStateBoolean("DisableAutoScreenRotation"));
        view.updateActivityMode();
    }

    private void openChapterFromLink(BibleReference osisLink) {
        mTask = new AsyncOpenChapter(progressMessage, false, librarian, osisLink);
        asyncManager.setupTask(mTask, this);
    }

    private void viewCurrentChapter() {
        view.disableActionMode();
        openChapterFromLink(librarian.getCurrentOSISLink());
    }

    public interface IReaderView {
        void disableActionMode();

        void setCurrentOrientation(boolean disableAutoRotation);

        void setKeepScreen(boolean isKeepScreen);

        void setNightMode(boolean isNightMode);

        boolean invertNightMode();

        void setReaderMode(ReaderWebView.Mode mode);

        void hideTTSPlayer();

        void openAboutActivity();

        void openBookmarkActivity(int requestCode);

        void openHelpActivity();

        void openLibraryActivity(int requestCode);

        void openHistoryActivity(int requestCode);

        void openSearchActivity(int requestCode);

        void openSettingsActivity(int requestCode);

        void openTagsActivity(int requestCode);

        void setContent(String baseUrl, String content, int verse, boolean isBible);

        void setTitle(String moduleName, String link);

        void viewTTSPlayer();

        void updateActivityMode();

        void updateContent();
    }
}
