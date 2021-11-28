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
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@SuppressWarnings("WeakerAccess")
public abstract class BasePresenter<T extends BaseView> {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
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

    @Nullable
    protected T getView() {
        return viewRef.get();
    }

    protected void getViewAndExecute(@NonNull Command<T> command) {
        T view = getView();
        if (view != null) {
            command.execute(view);
        }
    }

    /**
     * Команда для выполнения на View
     *
     * @param <T> тип класса View
     */
    protected interface Command<T> {

        void execute(@NonNull T view);
    }
}
