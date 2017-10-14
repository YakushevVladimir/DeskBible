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
 * File: BaseActivity.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.BibleQuote.R;
import com.BibleQuote.di.component.ActivityComponent;
import com.BibleQuote.domain.AnalyticsHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;

public abstract class BaseActivity<T extends BasePresenter> extends BQActivity implements BaseView {

    private static final int DELAY_SHOW_PROGRESS = 500;

    @Inject protected T presenter;
    @Inject protected AnalyticsHelper analyticsHelper;

    private ProgressDialog progressDialog;
    private Handler progressHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getRootLayout());
        ButterKnife.bind(this);
        attachView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onViewCreated();
    }

    @Override
    public void showProgress(boolean cancelable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(cancelable);
        progressDialog.setMessage(getString(R.string.messageLoad));
        if (!progressDialog.isShowing()) {
            progressHandler.postDelayed(() -> progressDialog.show(), DELAY_SHOW_PROGRESS);
        }
    }

    @Override
    public void hideProgress() {
        progressHandler.removeCallbacksAndMessages(null);
        if (!isFinishing() && progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }
    }

    @Override
    public void showToast(int resource) {
        Toast.makeText(this, resource, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    protected abstract void attachView();

    protected abstract int getRootLayout();

    protected abstract void inject(ActivityComponent component);
}
