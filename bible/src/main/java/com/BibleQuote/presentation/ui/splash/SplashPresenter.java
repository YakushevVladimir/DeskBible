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
 * File: SplashPresenter.java
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.splash;

import androidx.annotation.NonNull;

import com.BibleQuote.R;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.presentation.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.Completable;
import ru.churchtools.deskbible.domain.config.FeatureToggle;
import ru.churchtools.deskbible.domain.logger.StaticLogger;
import ru.churchtools.deskbible.domain.migration.UpdateManager;

public class SplashPresenter extends BasePresenter<SplashView> {

    @NonNull
    private final ILibraryController libraryController;
    @NonNull
    private final UpdateManager updateManager;
    @NonNull
    private final FeatureToggle featureToggle;

    @Inject
    SplashPresenter(@NonNull ILibraryController libraryController,
                    @NonNull UpdateManager updateManager,
                    @NonNull FeatureToggle featureToggle) {
        this.libraryController = libraryController;
        this.updateManager = updateManager;
        this.featureToggle = featureToggle;
    }

    @Override
    public void onViewCreated() {
        update();
    }

    void update() {
        SplashView view = getView();
        if (view == null) {
            return;
        }

        addSubscription(updateManager.update()
                .subscribeOn(view.backgroundThread())
                .observeOn(view.mainThread())
                .subscribe(
                        message -> getViewAndExecute(view1-> view1.showUpdateMessage(message)),
                        this::handleError,
                        this::initLibrary
                )
        );
    }

    private void handleError(Throwable throwable) {
        StaticLogger.error(this, "Update error", throwable);
        getViewAndExecute(view -> {
            view.showToast(R.string.error_initialization_failed);
            view.gotoReaderActivity();
        });
    }

    private void initLibrary() {
        SplashView view = getView();
        if (view == null) {
            return;
        }

        addSubscription(Completable.fromRunnable(libraryController::init)
                .concatWith(Completable.fromRunnable(featureToggle::initToggles))
                .subscribeOn(view.backgroundThread())
                .observeOn(view.mainThread())
                .subscribe(
                        () -> getViewAndExecute(SplashView::gotoReaderActivity),
                        this::handleError
                )
        );
    }
}
