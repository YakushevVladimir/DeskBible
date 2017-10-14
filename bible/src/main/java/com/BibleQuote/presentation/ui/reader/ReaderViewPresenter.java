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
 * File: ReaderViewPresenter.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.reader;

import android.net.Uri;

import com.BibleQuote.di.scope.PerActivity;
import com.BibleQuote.domain.AnalyticsHelper;
import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.textFormatters.ModuleTextFormatter;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.presentation.ui.base.BasePresenter;
import com.BibleQuote.presentation.ui.reader.tts.TTSPlayerFragment;
import com.BibleQuote.presentation.widget.ReaderWebView;
import com.BibleQuote.utils.PreferenceHelper;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

@PerActivity
public class ReaderViewPresenter extends BasePresenter<ReaderView> implements TTSPlayerFragment.OnTTSStopSpeakListener {

    private AnalyticsHelper analyticsHelper;
    private Librarian librarian;
    private PreferenceHelper preferenceHelper;

    @Inject
    ReaderViewPresenter(Librarian librarian, PreferenceHelper prefHelper, AnalyticsHelper helper) {
        this.librarian = librarian;
        this.preferenceHelper = prefHelper;
        this.analyticsHelper = helper;
    }

    @Override
    public void onStopSpeak() {
        getView().hideTTSPlayer();
    }

    @Override
    public void onViewCreated() {
        initView();
    }

    void inverseNightMode() {
        boolean nightMode = !preferenceHelper.getTextAppearance().isNightMode();
        preferenceHelper.setNightMode(nightMode);
        getView().setTextAppearance(preferenceHelper.getTextAppearance());
    }

    boolean isVolumeButtonsToScroll() {
        return preferenceHelper.volumeButtonsToScroll();
    }

    void nextChapter() {
        try {
            librarian.nextChapter();
            viewCurrentChapter();
        } catch (OpenModuleException e) {
            getView().onOpenChapterFailure(e);
        }
    }

    void onChangeSettings() {
        initView();
        getView().updateContent();
    }

    void onPause() {
        librarian.setCurrentVerseNumber(getView().getCurrVerse());
    }

    void onResume() {
        ReaderView view = getView();
        view.setKeepScreen(preferenceHelper.getBoolean("DisableTurnScreen"));
        view.setCurrentOrientation(preferenceHelper.getBoolean("DisableAutoScreenRotation"));
    }

    void openLastLink() {
        openChapterFromLink(new BibleReference(preferenceHelper.getLastRead()));
    }

    void openLink(String link) {
        openChapterFromLink(new BibleReference(link));
    }

    void openLink(Uri data) {
        openChapterFromLink(new BibleReference(data));
    }

    void prevChapter() {
        try {
            librarian.prevChapter();
            viewCurrentChapter();
        } catch (OpenModuleException e) {
            getView().onOpenChapterFailure(e);
        }
    }

    private void initView() {
        ReaderView view = getView();
        view.setTextAppearance(preferenceHelper.getTextAppearance());
        view.setReaderMode(preferenceHelper.isReadModeByDefault() ? ReaderWebView.Mode.Read : ReaderWebView.Mode.Study);
        view.setKeepScreen(preferenceHelper.getBoolean("DisableTurnScreen"));
        view.setCurrentOrientation(preferenceHelper.getBoolean("DisableAutoScreenRotation"));
        view.updateActivityMode();
    }

    private void openChapterFromLink(BibleReference osisLink) {
        if (!librarian.isOSISLinkValid(osisLink)) {
            getView().openLibraryActivity();
            return;
        }

        analyticsHelper.moduleEvent(osisLink);
        getView().showProgress(false);
        Disposable subscription = Single.just(osisLink)
                .subscribeOn(getView().backgroundThread())
                .map(link -> librarian.openChapter(link))
                .observeOn(getView().mainThread())
                .subscribe(
                        chapter -> {
                            ReaderView view = getView();
                            if (view == null) {
                                return;
                            }

                            BaseModule module = librarian.getCurrModule();
                            view.setTextFormatter(new ModuleTextFormatter(module, preferenceHelper));
                            view.setContent(librarian.getBaseUrl(), chapter, osisLink.getFromVerse(), module.isBible());
                            view.setTitle(osisLink.getModuleID(), librarian.getHumanBookLink());
                            view.hideProgress();
                        },
                        throwable -> {
                            ReaderView view = getView();
                            if (view != null) {
                                view.onOpenChapterFailure(throwable);
                            }
                        }
                );
        addSubscription(subscription);
    }

    private void viewCurrentChapter() {
        getView().disableActionMode();
        openChapterFromLink(librarian.getCurrentOSISLink());
    }
}
