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
 * File: CachedLibraryRepository.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.controller;

import com.BibleQuote.domain.controller.LibraryRepository;
import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.entity.ModuleList;
import com.BibleQuote.domain.exceptions.DataAccessException;
import com.BibleQuote.domain.repository.ICacheRepository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.churchtools.deskbible.domain.logger.StaticLogger;

public class CachedLibraryRepository implements LibraryRepository {

    private ICacheRepository cacheRepository;
    private CopyOnWriteArrayList<BaseModule> modules = new CopyOnWriteArrayList<>();

    public CachedLibraryRepository(ICacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    @Override
    public List<BaseModule> modules() {
        StaticLogger.info(this, "Get module list");
        if (modules.isEmpty() && cacheRepository.isCacheExist()) {
            try {
                modules.addAll(cacheRepository.getData());
            } catch (DataAccessException e) {
                StaticLogger.error(this, "Get module list failure", e);
            }
        }

        return modules;
    }

    @Override
    public void replace(Collection<BaseModule> list) {
        StaticLogger.info(this, "Replacing modules in the cache");
        modules.clear();
        modules.addAll(list);
        cacheModulesList();
    }

    @Override
    public void add(BaseModule module) {
        StaticLogger.info(this, "Adding a module to the cache");
        modules.add(module);
        cacheModulesList();
    }

    private void cacheModulesList() {
        try {
            cacheRepository.saveData(new ModuleList(modules));
        } catch (DataAccessException e) {
            StaticLogger.error(this, "Can't save modules to a cache.", e);
        }
    }
}
