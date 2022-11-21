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
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.net.Uri;

import com.BibleQuote.domain.AnalyticsHelper;
import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.textFormatters.ModuleTextFormatter;
import com.BibleQuote.entity.TextAppearance;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.presentation.widget.Mode;
import com.BibleQuote.utils.PreferenceHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.schedulers.Schedulers;
import ru.churchtools.deskbible.domain.config.FeatureToggle;

@RunWith(MockitoJUnitRunner.class)
public class ReaderViewPresenterTest {

    private static final String BASE_URL = "/root/path";
    private static final String HUMAN_LINK = "Gen 1:1";

    @Mock
    private AnalyticsHelper analyticsHelper;
    @Mock
    private Librarian librarian;
    @Mock
    private PreferenceHelper preferenceHelper;
    @Mock
    private ReaderView view;
    @Mock
    private FeatureToggle featureToggle;

    private ReaderViewPresenter presenter;

    @Before
    public void setUp() throws Exception {
        when(preferenceHelper.getTextAppearance()).thenReturn(mock(TextAppearance.class));
        when(preferenceHelper.isReadModeByDefault()).thenReturn(true);
        when(preferenceHelper.getBoolean(anyString())).thenReturn(true);

        when(view.mainThread()).thenReturn(Schedulers.trampoline());
        when(view.backgroundThread()).thenReturn(Schedulers.trampoline());

        presenter = new ReaderViewPresenter(
                librarian, preferenceHelper, analyticsHelper);
        presenter.attachView(view);
    }

    @Test
    public void inverseNightMode() {
        TextAppearance textAppearance = mock(TextAppearance.class);
        when(textAppearance.isNightMode()).thenReturn(false);
        when(preferenceHelper.getTextAppearance()).thenReturn(textAppearance);
        doAnswer(invocation -> {
            when(textAppearance.isNightMode()).thenReturn(invocation.getArgument(0));
            return null;
        }).when(preferenceHelper).setNightMode(anyBoolean());

        presenter.inverseNightMode();

        ArgumentCaptor<TextAppearance> captor = ArgumentCaptor.forClass(TextAppearance.class);
        verify(view).setTextAppearance(captor.capture());
        assertTrue(captor.getValue().isNightMode());
    }

    @Test
    public void isVolumeButtonsToScroll() {
        when(preferenceHelper.volumeButtonsToScroll()).thenReturn(false);
        assertFalse(presenter.isVolumeButtonsToScroll());
    }

    @Test
    public void nextChapter() {
        when(librarian.isOSISLinkValid(any())).thenReturn(false);
        presenter.nextChapter();
        verify(librarian).getCurrentOSISLink();
        verify(view).disableActionMode();
        verify(view).openLibraryActivity();
    }

    @Test
    public void nextChapterWithError() throws Exception {
        doAnswer(invocation -> {
            throw new OpenModuleException("RST", "");
        }).when(librarian).nextChapter();
        presenter.nextChapter();
        verify(view).onOpenChapterFailure(any());
    }

    @Test
    public void onChangeSettings() {
        presenter.onChangeSettings();
        verify(view, times(1)).setTextAppearance(any(TextAppearance.class));
        verify(view, times(1)).setReaderMode(any(Mode.class));
        verify(view, times(1)).setKeepScreen(anyBoolean());
        verify(view, times(1)).setCurrentOrientation(anyBoolean());
        verify(view, times(1)).updateActivityMode();
        verify(view).updateContent();
    }

    @Test
    public void onPause() {
        when(view.getCurrVerse()).thenReturn(10);
        presenter.onPause();
        verify(librarian).setCurrentVerseNumber(eq(10));
    }

    @Test
    public void onResume() {
        when(preferenceHelper.getBoolean(eq("DisableTurnScreen"))).thenReturn(false);
        when(preferenceHelper.getBoolean(eq("DisableAutoScreenRotation"))).thenReturn(true);
        presenter.onResume();
        verify(view).setKeepScreen(eq(false));
        verify(view).setCurrentOrientation(eq(true));
    }

    @Test
    public void onStopSpeak() {
        presenter.onStopSpeak();
        verify(view).hideTTSPlayer();
    }

    @Test
    public void onViewCreated() {
        presenter.onViewCreated();
        verify(view, times(1)).setTextAppearance(any(TextAppearance.class));
        verify(view, times(1)).setReaderMode(any(Mode.class));
        verify(view, times(1)).setKeepScreen(anyBoolean());
        verify(view, times(1)).setCurrentOrientation(anyBoolean());
        verify(view, times(1)).updateActivityMode();
    }

    @Test
    public void openLastLink() {
        when(librarian.isOSISLinkValid(any())).thenReturn(false);
        when(preferenceHelper.getLastRead()).thenReturn("RST.Gen.1");
        presenter.openLastLink();
        verify(preferenceHelper).getLastRead();
        verify(librarian).isOSISLinkValid(any());
        verify(view).openLibraryActivity();
    }

    @Test
    public void openLinkFromUri() throws Exception {
        Uri deeplink = mock(Uri.class);
        when(deeplink.getPath()).thenReturn("/Gen/1_1/RST");

        initMocks();
        when(librarian.openChapter(any())).thenReturn(mock(Chapter.class));

        presenter.openLink(deeplink);

        ArgumentCaptor<BibleReference> captorBR = ArgumentCaptor.forClass(BibleReference.class);
        verify(librarian).openChapter(captorBR.capture());
        assertEquals("RST.Gen.1.1", captorBR.getValue().getPath());

        verify(librarian).getCurrModule();
        verify(view).setTextFormatter(any(ModuleTextFormatter.class));
        verify(view).setContent(eq(BASE_URL), any(Chapter.class), eq(1), eq(true));
        verify(view).setTitle(eq("RST"), eq(HUMAN_LINK));

        InOrder progressOrder = inOrder(view);
        progressOrder.verify(view).showProgress(eq(false));
        progressOrder.verify(view).hideProgress();
    }

    @Test
    public void openLinkStringWithClosedActivity() throws Exception {
        initMocks();
        doAnswer(invocation -> {
            presenter.detachView();
            return mock(Chapter.class);
        }).when(librarian).openChapter(any());

        presenter.openLink("RST.Gen.1.1");

        verify(view).showProgress(eq(false));
        verify(view, never()).setTitle(anyString(), anyString());
        verify(view, never()).onOpenChapterFailure(any());
    }

    @Test
    public void openLinkStringWithError() throws Exception {
        initMocks();
        when(librarian.openChapter(any())).thenThrow(new OpenModuleException("RST", ""));

        presenter.openLink("RST.Gen.1.1");

        InOrder progressOrder = inOrder(view);
        progressOrder.verify(view).showProgress(eq(false));
        progressOrder.verify(view).onOpenChapterFailure(any(OpenModuleException.class));
    }

    @Test
    public void prevChapter() {
        when(librarian.isOSISLinkValid(any())).thenReturn(false);
        presenter.prevChapter();
        verify(librarian).getCurrentOSISLink();
        verify(view).disableActionMode();
        verify(view).openLibraryActivity();
    }

    @Test
    public void prevChapterWithError() throws Exception {
        doAnswer(invocation -> {
            throw new OpenModuleException("RST", "");
        }).when(librarian).prevChapter();
        presenter.prevChapter();
        verify(view).onOpenChapterFailure(any());
    }

    private void initMocks() {
        final BaseModule module = mock(BaseModule.class);
        when(module.isBible()).thenReturn(true);
        when(module.isContainsStrong()).thenReturn(false);

        doAnswer(invocation -> {
            BibleReference reference = invocation.getArgument(0);
            return reference.getPath() != null;
        }).when(librarian).isOSISLinkValid(any());
        when(librarian.getCurrModule()).thenReturn(module);
        when(librarian.getBaseUrl()).thenReturn(BASE_URL);
        when(librarian.getHumanBookLink()).thenReturn(HUMAN_LINK);
    }
}