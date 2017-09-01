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
 * File: FsLibraryController.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.controller;

import android.util.Log;

import com.BibleQuote.domain.controller.ICacheModuleController;
import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.entity.ModuleList;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.repository.ILibraryRepository;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class FsLibraryController implements ILibraryController {

    private static final String TAG = FsLibraryController.class.getSimpleName();
    private ICacheModuleController cache;
    private ILibraryRepository<? extends Module> libraryRepository;
    private Map<String, Module> moduleSet = Collections.synchronizedMap(new TreeMap<String, Module>());

    public FsLibraryController(ILibraryRepository<? extends Module> repository, ICacheModuleController cacheModuleController) {
        this.libraryRepository = repository;
        this.cache = cacheModuleController;
    }

    @Override
    public void init() {
        if (moduleSet.isEmpty() && cache.isCacheExist()) {
            Log.i(TAG, "....Load modules from cache");
            loadCachedModules();
        }
        if (moduleSet.isEmpty()) {
            reloadModules();
        }
    }

    @Override
    public Map<String, Module> reloadModules() {
        moduleSet.clear();
        moduleSet.putAll(libraryRepository.loadFileModules());
        cache.saveModuleList(new ModuleList(moduleSet.values()));
        return moduleSet;
    }

    @Override
    public Map<String, Module> getModules() {
        return moduleSet;
    }

    @Override
    public Module getModuleByID(String moduleID) throws OpenModuleException {
        if (moduleID == null) {
            return null;
        }
        Module module = moduleSet.get(moduleID);
        if (module == null) {
            throw new OpenModuleException(moduleID, null);
        }
        return module;
    }

    @Override
    public void loadModule(String path) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        Module module = libraryRepository.loadModule(path);
        moduleSet.put(module.getID(), module);
    }

    private void loadCachedModules() {
        ModuleList moduleList = cache.getModuleList();
        moduleSet.clear();
        for (Module fsModule : moduleList) {
            moduleSet.put(fsModule.getID(), fsModule);
        }
    }
}
