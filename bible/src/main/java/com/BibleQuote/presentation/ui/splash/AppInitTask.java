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
 * File: AppInitTask.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.splash;

import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.presentation.ui.base.BaseView;
import com.BibleQuote.utils.UpdateManager;

import java.lang.ref.WeakReference;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposables;

class AppInitTask extends Single<BaseView> {

    private final WeakReference<BaseView> viewRef;
    private final ILibraryController libraryController;
    private final UpdateManager updateManager;

    AppInitTask(BaseView view, UpdateManager updateManager, ILibraryController libraryController) {
        this.viewRef = new WeakReference<>(view);
        this.libraryController = libraryController;
        this.updateManager = updateManager;
    }

    @Override
    protected void subscribeActual(@io.reactivex.annotations.NonNull SingleObserver<? super BaseView> observer) {
        observer.onSubscribe(Disposables.disposed());
        try {
            BaseView view = viewRef.get();
            if (view != null) {
                updateManager.start();
                libraryController.init();
            }
            observer.onSuccess(viewRef.get());
        } catch (Throwable th) {
            observer.onError(th);
        }
    }
}
