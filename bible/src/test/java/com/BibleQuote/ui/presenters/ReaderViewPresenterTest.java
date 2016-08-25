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
 * File: ReaderViewPresenterTest.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 *
 */

package com.BibleQuote.ui.presenters;

import android.content.Context;

import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.ui.widget.ReaderWebView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ReaderViewPresenterTest {

    private ReaderViewPresenter.IReaderView view;
    private Context context;
    private Librarian librarian;

    @Test
    public void testInitView() throws Exception {
        new ReaderViewPresenter(context, view, librarian);
        verify(view, times(1)).setNightMode(anyBoolean());
        verify(view, times(1)).setReaderMode(any(ReaderWebView.Mode.class));
        verify(view, times(1)).setKeepScreen(anyBoolean());
        verify(view, times(1)).setCurrentOrientation(anyBoolean());
        verify(view, times(1)).updateActivityMode();
    }

    @Test
    public void testSetOSISLink_invalidLink() throws Exception {
        ReaderViewPresenter presenter = new ReaderViewPresenter(context, view, librarian);
        when(librarian.isOSISLinkValid(any(BibleReference.class))).thenReturn(false);
        BibleReference ref = mock(BibleReference.class);
        when(ref.getPath()).thenReturn("RST:Gen.1.1");
        presenter.setOSISLink(ref);
        verify(librarian, times(1)).isOSISLinkValid(any(BibleReference.class));
        verify(view).openLibraryActivity(anyInt());
    }

    @Test
    public void testPrevChapter() throws Exception {

    }

    @Test
    public void testNextChapter() throws Exception {

    }

    @Test
    public void testIsVolumeButtonsToScroll() throws Exception {

    }

    @Test
    public void testOnPause() throws Exception {

    }

    @Test
    public void testOnResume() throws Exception {

    }

    @Test
    public void testOnOptionsItemSelected() throws Exception {

    }

    @Test
    public void testOnNavigationItemSelected() throws Exception {

    }

    @Test
    public void testOnConfigurationChanged() throws Exception {

    }

    @Test
    public void testOnActivityResult() throws Exception {

    }

    @Test
    public void testGetContext() throws Exception {

    }

    @Test
    public void testOnTaskComplete() throws Exception {

    }

    @Test
    public void testOnStopSpeak() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        view = mock(ReaderViewPresenter.IReaderView.class);
        context = mock(Context.class);
        librarian = mock(Librarian.class);
    }
}