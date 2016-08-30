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
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.BibleQuote.dal.repository.FsCacheRepository;
import com.BibleQuote.dal.repository.FsLibraryRepository;
import com.BibleQuote.domain.controllers.cache.FsCacheModuleController;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.entity.ModuleList;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.repository.ILibraryRepository;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.DataConstants;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class FsLibraryController implements ILibraryController {

    private static final String TAG = FsLibraryController.class.getSimpleName();
    private static volatile FsLibraryController instance;

    private ILibraryRepository<BQModule> mRepository;
    private Map<String, Module> moduleSet = Collections.synchronizedMap(new TreeMap<String, Module>());
    private FsCacheModuleController cache;

    private FsLibraryController(FsCacheModuleController cache) {
        mRepository = new FsLibraryRepository(getLibraryDir());
        this.cache = cache;
    }

    public static FsLibraryController getInstance(Context context) {
        if (instance == null) {
            synchronized (FsLibraryController.class) {
                FsCacheModuleController cache = new FsCacheModuleController(new FsCacheRepository(context));
                instance = new FsLibraryController(cache);
            }
        }
        return instance;
    }

    @Override
    public Map<String, Module> loadModules() {
        moduleSet.clear();
        moduleSet.putAll(mRepository.loadFileModules());
        cache.saveModuleList(getModuleList(moduleSet));
        return moduleSet;
    }

    @Override
    public Map<String, Module> getModules() {
        if (moduleSet.isEmpty() && cache.isCacheExist()) {
            Log.i(TAG, "....Load modules from cache");
            loadCachedModules();
            if (moduleSet.isEmpty()) {
                loadModules();
            }
        }
        return moduleSet;
    }

    @Override
    public Module getModuleByID(String moduleID) throws OpenModuleException {
        if (moduleID == null) {
            return null;
        }
        return moduleSet.get(moduleID);
    }

    @Override
    public void loadModule(String path) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        Module module = mRepository.loadModule(path);
        moduleSet.put(module.getID(), module);
    }

    @NonNull
    private static File getLibraryDir() {
        File result = new File(DataConstants.getLibraryPath());
        if (!result.exists()) {
            boolean deleted = result.mkdir();
            if (!deleted) {
                Log.e(TAG, "Don't remove library directory");
            }
        }
        return result;
    }

    private void loadCachedModules() {
        ModuleList moduleList = cache.getModuleList();
        moduleSet.clear();
        for (Module fsModule : moduleList) {
            moduleSet.put(fsModule.getID(), fsModule);
        }
    }

    private ModuleList getModuleList(Map<String, Module> moduleSet) {
        ModuleList result = new ModuleList();
        for (Module currModule : moduleSet.values()) {
            result.add(currModule);
        }
        return result;
    }
}
