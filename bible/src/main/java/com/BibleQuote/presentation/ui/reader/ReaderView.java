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
 * File: ReaderView.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.reader;

import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.textFormatters.ITextFormatter;
import com.BibleQuote.entity.TextAppearance;
import com.BibleQuote.presentation.ui.base.BaseView;
import com.BibleQuote.presentation.widget.ReaderWebView;

interface ReaderView extends BaseView {

    int getCurrVerse();

    void setCurrentOrientation(boolean disableAutoRotation);

    void setKeepScreen(boolean isKeepScreen);

    void setReaderMode(ReaderWebView.Mode mode);

    void setTextAppearance(TextAppearance textAppearance);

    void setTextFormatter(ITextFormatter formatter);

    void disableActionMode();

    void hideTTSPlayer();

    void onOpenChapterFailure(Throwable ex);

    void openLibraryActivity();

    void setContent(String baseUrl, Chapter chapter, int verse, boolean isBible);

    void setTitle(String moduleName, String link);

    void updateActivityMode();

    void updateContent();

    void viewTTSPlayer();
}
