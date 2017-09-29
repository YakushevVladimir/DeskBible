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
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.presentation.activity.splash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.AsyncTaskManager;
import com.BibleQuote.async.OnTaskCompleteListener;
import com.BibleQuote.async.task.command.AsyncCommand;
import com.BibleQuote.presentation.activity.base.BQActivity;
import com.BibleQuote.presentation.activity.reader.ReaderActivity;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Task;
import com.BibleQuote.utils.UpdateManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BQActivity implements OnTaskCompleteListener {

    private static final int REQUEST_PERMISSIONS = 1;

    @BindView(R.id.root_layout) FrameLayout rootLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(rootLayout, R.string.msg_permission_denied, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry, v -> checkPermissions())
                            .show();
                    return;
                }
            }
            startUpdateManager();
        }
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

    private void checkPermissions() {
        int state = PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (state != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        } else {
            startUpdateManager();
        }
    }

    private void startUpdateManager() {
        new AsyncTaskManager(this).setupTask(new AsyncCommand(() -> {
            PreferenceHelper preferenceHelper = BibleQuoteApp.getInstance().getPrefHelper();
            UpdateManager.start(SplashActivity.this, preferenceHelper);
            BibleQuoteApp.getInstance().getLibraryController().init();
            return true;
        }, null, true));
    }
}