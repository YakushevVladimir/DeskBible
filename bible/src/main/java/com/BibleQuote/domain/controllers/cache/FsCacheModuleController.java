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
 * File: FsCacheModuleController.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.controllers.cache;

import com.BibleQuote.domain.entity.ModuleList;
import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.repository.ICacheRepository;
import com.BibleQuote.utils.Logger;

public class FsCacheModuleController implements ICacheModuleController {
    private static final String TAG = "FsCacheRepository";

    private ICacheRepository cacheRepository;

    public FsCacheModuleController(ICacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    @Override
    public ModuleList getModuleList() {
        Logger.i(TAG, "Get module list");
        try {
            return cacheRepository.getData();
        } catch (DataAccessException e) {
            return new ModuleList();
        }
    }

    @Override
    public void saveModuleList(ModuleList moduleList) {
        Logger.i(TAG, "Save modules list to cache");
        try {
            cacheRepository.saveData(moduleList);
        } catch (DataAccessException e) {
            Logger.e(TAG, "Can't save modules to a cache.", e);
        }
    }

    @Override
    public boolean isCacheExist() {
        return cacheRepository.isCacheExist();
    }
}
