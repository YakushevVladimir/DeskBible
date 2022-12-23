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
 * File: ImagePreviewPresenterTest.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.imagepreview;

import com.BibleQuote.managers.Librarian;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Ignore("Класс устарел")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImagePreviewPresenterTest {

    @Mock Librarian librarian;
//    @Mock ImagePreviewView view;
//    private ImagePreviewPresenter presenter;

    @Before
    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        presenter = new ImagePreviewPresenter(librarian);
//        presenter.attachView(view);
    }

    @Test
    public void setImagePath() throws Exception {
//        presenter.setImagePath(null);
//        presenter.setImagePath("");
    }

    @Test
    public void onViewCreatedWithNullImagePath() throws Exception {
//        presenter.setImagePath(null);
//        presenter.onViewCreated();
//        verify(view).imageNotFound();
    }

    @Test
    public void onViewCreatedWithoutBitmap() throws Exception {
//        presenter.setImagePath("");
//        when(librarian.getModuleImage(anyString())).thenReturn(null);
//        presenter.onViewCreated();
//        verify(view).imageNotFound();
    }

    @Test
    public void onViewCreatedWithBitmap() throws Exception {
//        presenter.setImagePath("");
//        when(librarian.getModuleImage(anyString())).thenReturn(mock(Bitmap.class));
//        presenter.onViewCreated();
//        verify(view, never()).imageNotFound();
//        verify(view).updatePreviewDrawable(any());
    }
}