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
 * File: AsyncOpenChapter.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.async.task;

import android.os.Handler;
import android.util.Log;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.utils.Task;

public class AsyncOpenChapter extends Task {
	private static final String TAG = "AsyncOpenChapter";

	private BibleReference link;
	private Exception exception;
    private Handler handler;

    public AsyncOpenChapter(BibleReference link, String message) {
        super(message, true);
        this.link = link;
        this.handler = new Handler();
    }

	@Override
    protected Boolean doInBackground(String... arg0) {
        try {
            Log.i(TAG, String.format("Open OSIS link %s", link.getPath()));
            handler.postDelayed(() -> setHidden(false), 500);
            BibleQuoteApp.getInstance().getLibrarian().openChapter(link);
            handler.removeCallbacksAndMessages(null);
            return true;
        } catch (OpenModuleException | BookNotFoundException e) {
            exception = e;
        }
        return false;
    }

	public Exception getException() {
		return exception;
	}
}
