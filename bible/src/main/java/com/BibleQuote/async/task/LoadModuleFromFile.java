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
 * File: LoadModuleFromFile.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.async.task;

import android.content.Context;

import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.NotifyDialog;
import com.BibleQuote.utils.Task;

/**
 * @author ru_phoenix
 * @version 1.0
 */
public class LoadModuleFromFile extends Task {

	private Context context;
	private String errorMessage = "";
	private String path;

	public LoadModuleFromFile(String message, Boolean isHidden, Context context, String path) {
		super(message, isHidden);
		this.context = context;
		this.path = path;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			FsUtils.addModuleFromFile(context, path);
		} catch (Exception e) {
			errorMessage = e.getMessage();
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (!result) {
			new NotifyDialog(errorMessage, context).show();
		}
	}
}
