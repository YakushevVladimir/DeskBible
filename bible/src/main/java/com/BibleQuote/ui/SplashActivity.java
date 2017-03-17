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
 * File: SplashActivity.java
 *
 * Created by Vladimir Yakushev at 3/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.AsyncTaskManager;
import com.BibleQuote.async.OnTaskCompleteListener;
import com.BibleQuote.async.task.command.AsyncCommand;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;
import com.BibleQuote.utils.UpdateManager;

public class SplashActivity extends Activity implements OnTaskCompleteListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncTaskManager(this).setupTask(new AsyncCommand(new AsyncCommand.ICommand() {
            @Override
            public boolean execute() throws Exception {
                PreferenceHelper preferenceHelper = BibleQuoteApp.getInstance().getPrefHelper();
                UpdateManager.start(SplashActivity.this, preferenceHelper);
                BibleQuoteApp.getInstance().getLibraryController().init();
                return true;
            }
        }, null, true));
    }

    @Override
    public void onTaskComplete(Task task) {
        startActivity(new Intent(this, ReaderActivity.class));
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }
}