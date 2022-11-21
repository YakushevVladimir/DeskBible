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
 * File: MockModuleRepository.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.search;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.repository.IModuleRepository;

import java.io.File;

class MockModuleRepository implements IModuleRepository<BaseModule> {

    @Override
    public Bitmap getBitmap(BaseModule module, String path) {
        return null;
    }

    @Override
    public Chapter loadChapter(BaseModule module, String bookID, int chapter) {
        return null;
    }

    @Override
    public BaseModule loadModule(File path) {
        return null;
    }

    @NonNull
    @Override
    public String getBookContent(BaseModule module, String bookID) {
        return "";
    }
}
