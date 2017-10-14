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
 * File: ImagePreviewPresenter.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.imagepreview;

import android.graphics.Bitmap;

import com.BibleQuote.di.scope.PerActivity;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.presentation.ui.base.BasePresenter;

import javax.inject.Inject;

@PerActivity
class ImagePreviewPresenter extends BasePresenter<ImagePreviewView> {

    private Librarian librarian;
    private String imagePath;

    @Inject
    ImagePreviewPresenter(Librarian librarian) {
        this.librarian = librarian;
    }

    void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public void onViewCreated() {
        if (imagePath == null) {
            getView().imageNotFound();
            return;
        }

        Bitmap bitmap = librarian.getModuleImage(imagePath);
        if (bitmap == null) {
            getView().imageNotFound();
        } else {
            getView().updatePreviewDrawable(bitmap);
        }
    }
}
