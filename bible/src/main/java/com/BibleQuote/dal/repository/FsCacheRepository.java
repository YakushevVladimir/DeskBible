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
 * File: FsCacheRepository.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository;

import android.content.Context;

import com.BibleQuote.domain.entity.ModuleList;
import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.repository.ICacheRepository;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FsCacheRepository implements ICacheRepository {

    private static final String TAG = "FsCacheRepository";

    private File cacheDir;
    private String cacheName;

    public FsCacheRepository(Context context) {
        this.cacheDir = context.getCacheDir();
        this.cacheName = DataConstants.LIBRARY_CACHE;
    }

    @Override
    public ModuleList getData() throws DataAccessException {
        Log.i(TAG, "Loading data from a file system cache.");
        ModuleList result;
        try {
            FileInputStream fStr = new FileInputStream(new File(cacheDir, cacheName));
            ObjectInputStream out = new ObjectInputStream(fStr);
            result = (ModuleList) out.readObject();
            out.close();
        } catch (ClassNotFoundException e) {
            String message = String.format("Unexpected data format in the cache %1$s%2$s: %3$s",
                    cacheDir, cacheName, e.getMessage());
            throw new DataAccessException(message);
        } catch (IOException e) {
            String message = String.format("Data isn't loaded from the cache %1$s%2$s: %3$s",
                    cacheDir, cacheName, e.getMessage());
            throw new DataAccessException(message);
        } catch (ClassCastException e) {
            String message = String.format("Data isn't cast to ModuleList from the cache %1$s%2$s: %3$s",
                    cacheDir, cacheName, e.getMessage());
            throw new DataAccessException(message);
        }

        return result;
    }

    @Override
    public void saveData(ModuleList data) throws DataAccessException {
        Log.i(TAG, "Save modules to a file system cache.");
        try {
            FileOutputStream fStr = new FileOutputStream(new File(cacheDir, cacheName));
            ObjectOutputStream out = new ObjectOutputStream(fStr);
            out.writeObject(data);
            out.close();
        } catch (IOException e) {
            String message = String.format("Data isn't stored in the cache %1$s%2$s: %3$s",
                    cacheDir, cacheName, e.getMessage());
            throw new DataAccessException(message);
        }
    }

    @Override
    public boolean isCacheExist() {
        File cache = new File(cacheDir, cacheName);
        return cache.exists();
    }
}
