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
 * File: ReaderViewPresenterTest.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.ui.presenters;

import android.content.Context;

import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.entity.TextAppearance;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.ui.widget.ReaderWebView;
import com.BibleQuote.utils.PreferenceHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ReaderViewPresenterTest {

    @Mock PreferenceHelper prefHelper;
    @Mock ReaderViewPresenter.IReaderView view;
    @Mock Context context;
    @Mock Librarian librarian;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(prefHelper.getTextAppearance()).thenReturn(mock(TextAppearance.class));
        when(prefHelper.isReadModeByDefault()).thenReturn(true);
        when(prefHelper.getBoolean(anyString())).thenReturn(true);
    }

    @Test
    public void testGetContext() throws Exception {

    }

    @Test
    public void testInitView() throws Exception {
        new ReaderViewPresenter(context, view, librarian, prefHelper);
        verify(view, times(1)).setTextAppearance(any(TextAppearance.class));
        verify(view, times(1)).setReaderMode(eq(ReaderWebView.Mode.Read));
        verify(view, times(1)).setKeepScreen(anyBoolean());
        verify(view, times(1)).setCurrentOrientation(anyBoolean());
        verify(view, times(1)).updateActivityMode();
    }

    @Test
    public void testIsVolumeButtonsToScroll() throws Exception {

    }

    @Test
    public void testNextChapter() throws Exception {

    }

    @Test
    public void testOnActivityResult() throws Exception {

    }

    @Test
    public void testOnConfigurationChanged() throws Exception {

    }

    @Test
    public void testOnNavigationItemSelected() throws Exception {

    }

    @Test
    public void testOnOptionsItemSelected() throws Exception {

    }

    @Test
    public void testOnPause() throws Exception {

    }

    @Test
    public void testOnResume() throws Exception {

    }

    @Test
    public void testOnStopSpeak() throws Exception {

    }

    @Test
    public void testOnTaskComplete() throws Exception {

    }

    @Test
    public void testPrevChapter() throws Exception {

    }

    @Test
    public void testSetOSISLink_invalidLink() throws Exception {
        ReaderViewPresenter presenter = new ReaderViewPresenter(context, view, librarian, prefHelper);
        when(librarian.isOSISLinkValid(any(BibleReference.class))).thenReturn(false);
        BibleReference ref = mock(BibleReference.class);
        when(ref.getPath()).thenReturn("RST:Gen.1.1");
        presenter.setOSISLink(ref);
        verify(librarian, times(1)).isOSISLinkValid(any(BibleReference.class));
        verify(view).openLibraryActivity(anyInt());
    }
}