/*
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
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: FsCacheModuleController.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.controllers;

import com.BibleQuote.dal.FsCacheContext;
import com.BibleQuote.dal.repository.FsCacheRepository;
import com.BibleQuote.dal.repository.ICacheRepository;
import com.BibleQuote.domain.controllers.ICacheModuleController;
import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.utils.Log;

import java.util.ArrayList;

public class FsCacheModuleController<TModule> implements ICacheModuleController<TModule> {
    private static final String TAG = "FsCacheRepository";

    private ICacheRepository<ArrayList<TModule>> cacheRepository;

    public FsCacheModuleController(FsCacheContext cacheContext) {
        this.cacheRepository = getCacheRepository(cacheContext);
    }

    @Override
    public ArrayList<TModule> getModuleList() {
        Log.i(TAG, "Get module list");
        try {
            return cacheRepository.getData();
        } catch (DataAccessException e) {
            return new ArrayList<TModule>();
        }
    }

    @Override
    public void saveModuleList(ArrayList<TModule> moduleList) {
        Log.i(TAG, "Save modules list to cache");
        try {
            cacheRepository.saveData(moduleList);
        } catch (DataAccessException e) {
            Log.e(TAG, "Can't save modules to a cache.", e);
        }
    }

    @Override
    public boolean isCacheExist() {
        return cacheRepository.isCacheExist();
    }

    private ICacheRepository<ArrayList<TModule>> getCacheRepository(FsCacheContext cacheContext) {
        if (this.cacheRepository == null) {
            this.cacheRepository = new FsCacheRepository<ArrayList<TModule>>(cacheContext);
        }
        return cacheRepository;
    }
}
