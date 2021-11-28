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
 * File: SplashPresenterTest.java
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.splash;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.BibleQuote.domain.controller.ILibraryController;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import ru.churchtools.deskbible.domain.config.FeatureToggle;
import ru.churchtools.deskbible.domain.migration.UpdateManager;

public class SplashPresenterTest {

    @Mock private ILibraryController libraryController;
    @Mock private UpdateManager updateManager;
    @Mock private SplashView view;
    @Mock private FeatureToggle featureToggle;

    private SplashPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new SplashPresenter(libraryController, updateManager, featureToggle);
        presenter.attachView(view);
        presenter.onViewCreated();

        when(view.backgroundThread()).thenReturn(Schedulers.trampoline());
        when(view.mainThread()).thenReturn(Schedulers.trampoline());
        when(featureToggle.newLibraryUiEnabled()).thenReturn(false);
    }

    @Test
    public void update() {
        when(updateManager.update()).thenReturn(Observable.fromArray(1, 2, 3));
        presenter.update();
        verify(view, times(3)).showUpdateMessage(anyInt());
        verify(libraryController).init();
        verify(view).gotoReaderActivity();
    }

    @Test
    public void updateWithError() {
        when(updateManager.update()).thenReturn(Observable.error(new IOException("abort")));
        presenter.update();
        verify(view).showToast(anyInt());
        verify(view).gotoReaderActivity();
    }
}