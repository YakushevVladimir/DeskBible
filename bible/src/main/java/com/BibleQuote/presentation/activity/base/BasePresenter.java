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
 * File: BasePresenter.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.activity.base;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@SuppressWarnings("WeakerAccess")
public abstract class BasePresenter<T extends BaseView> {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private WeakReference<T> viewRef;

    public void attachView(T view) {
        viewRef = new WeakReference<>(view);
    }

    public void detachView() {
        viewRef.clear();
        compositeDisposable.clear();
    }

    public abstract void onViewCreated();

    protected void addSubscription(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    protected T getView() {
        return viewRef.get();
    }
}
