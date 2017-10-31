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
 * File: BaseFragment.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.di.component.FragmentComponent;
import com.BibleQuote.di.module.FragmentModule;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView {

    @Inject protected T presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(getFragmentComponent());
        attachView();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onViewCreated();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detachView();
    }

    @Override
    public Scheduler backgroundThread() {
        return Schedulers.newThread();
    }

    @Override
    public Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public void showProgress(boolean cancelable) {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showToast(int resource) {
        Toast.makeText(getContext(), resource, Toast.LENGTH_SHORT).show();
    }

    protected abstract void inject(FragmentComponent component);

    protected abstract void attachView();

    protected void detachView() {
        presenter.detachView();
    }

    private FragmentComponent getFragmentComponent() {
        return BibleQuoteApp.instance(getContext())
                .getAppComponent()
                .fragmentComponent(new FragmentModule());
    }
}
