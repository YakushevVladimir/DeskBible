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
 * File: BQActivity.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.di.component.ActivityComponent;
import com.BibleQuote.di.module.ActivityModule;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.churchtools.deskbible.di.app.AppComponent;
import ru.churchtools.deskbible.domain.logger.StaticLogger;

public abstract class BQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(getActivityComponent());
        StaticLogger.info(this, "Create activity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        StaticLogger.info(this, "Start activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        StaticLogger.info(this, "Stop activity");
    }

    public Scheduler backgroundThread() {
        return Schedulers.newThread();
    }

    public Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    protected abstract void inject(ActivityComponent component);

    protected ActivityComponent getActivityComponent() {
        AppComponent appComponent = BibleQuoteApp.instance(this).getAppComponent();
        return appComponent.activityComponent(new ActivityModule());
    }
}
