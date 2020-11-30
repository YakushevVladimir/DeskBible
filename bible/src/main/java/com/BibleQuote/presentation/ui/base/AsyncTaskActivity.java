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
 * File: AsyncTaskActivity.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.async.OnTaskCompleteListener;
import com.BibleQuote.utils.Task;

/**
 * @author Vladimir Yakushev
 * @version 1.0
 */
public abstract class AsyncTaskActivity extends BQActivity implements OnTaskCompleteListener {

    protected AsyncManager mAsyncManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAsyncManager();
    }

    @Override
    public abstract void onTaskComplete(Task task);

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mAsyncManager.retainTask();
    }

    private void initAsyncManager() {
        mAsyncManager = BibleQuoteApp.getInstance().getAsyncManager();
        Object retainedTask = getLastCustomNonConfigurationInstance();
        if (retainedTask != null && retainedTask instanceof Task) {
            mAsyncManager.handleRetainedTask((Task) retainedTask, this);
        }
    }
}
