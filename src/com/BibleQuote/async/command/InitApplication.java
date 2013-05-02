/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.async.command;

import android.app.Activity;
import android.content.Context;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.async.AsyncCommand;
import com.BibleQuote.utils.Log;

public class InitApplication implements AsyncCommand.ICommand {
	private final String TAG = "InitApplication";
	private Context context;

	public InitApplication(Context context) {
		this.context = context;
	}

	@Override
	public void execute() throws Exception {
		Log.i(TAG, "Task InitApplication execute...");
		BibleQuoteApp app = (BibleQuoteApp) ((Activity) context).getApplication();
		app.Init();
	}
}
