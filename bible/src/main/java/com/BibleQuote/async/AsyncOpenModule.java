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
 * File: AsyncOpenModule.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.async;

import android.util.Log;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.domain.controllers.ILibraryController;
import com.BibleQuote.domain.entity.BibleReference;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.utils.Task;

public class AsyncOpenModule extends Task {
	private static final String TAG = "AsyncOpenBooks";

	private BibleReference link;
	private Exception exception;
	private Module module;
	private ILibraryController libCtrl;

	public AsyncOpenModule(String message, Boolean isHidden, BibleReference link) {
		super(message, isHidden);
		this.libCtrl = BibleQuoteApp.getInstance().getLibraryController();
		this.link = link;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			Log.i(TAG, String.format("Open OSIS link with moduleID=%1$s", link.getModuleID()));
			module = libCtrl.getModuleByID(link.getModuleID());
		} catch (OpenModuleException e) {
			Log.e(TAG, String.format("AsyncOpenBooks(): %s", e.toString()), e);
			exception = e;
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	public Exception getException() {
		return exception;
	}

	public Module getModule() {
		return module;
	}

}
