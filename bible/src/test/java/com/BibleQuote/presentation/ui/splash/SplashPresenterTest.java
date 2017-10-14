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
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.splash;

import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.utils.UpdateManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplashPresenterTest {

    @Mock ILibraryController libraryController;
    @Mock UpdateManager updateManager;
    @Mock SplashView view;

    private SplashPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new SplashPresenter(libraryController, updateManager);
        presenter.attachView(view);
        presenter.onViewCreated();

        when(view.backgroundThread()).thenReturn(Schedulers.trampoline());
        when(view.mainThread()).thenReturn(Schedulers.trampoline());
    }

    @Test
    public void update() throws Exception {
        presenter.update();
        verify(updateManager).start();
        verify(libraryController).init();
        verify(view).gotoReaderActivity();
    }

    @Test
    public void updateWithError() throws Exception {
        doAnswer(invocation -> {
            throw new RuntimeException();
        }).when(updateManager).start();

        presenter.update();
        verify(view).gotoReaderActivity();
    }
}