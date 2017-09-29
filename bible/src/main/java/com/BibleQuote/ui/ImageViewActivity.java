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
 * File: ImageViewActivity.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.ui.base.BaseActivity;
import com.BibleQuote.ui.widget.TouchImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageViewActivity extends BaseActivity {

    public static String IMAGE_PATH;

    @BindView(R.id.image) TouchImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ButterKnife.bind(this);

        imageView.setMaxZoom(10);

        if (TextUtils.isEmpty(IMAGE_PATH)) {
            finish();
        }

        Librarian librarian = BibleQuoteApp.getInstance().getLibrarian();
        Bitmap image = librarian.getModuleImage(IMAGE_PATH);
        if (image == null) {
            Toast.makeText(this, R.string.image_not_found, Toast.LENGTH_LONG).show();
            finish();
        } else {
            imageView.setImageDrawable(new BitmapDrawable(getResources(), image));
        }
    }

    @Override
    protected void onDestroy() {
        IMAGE_PATH = null;
        super.onDestroy();
    }
}
