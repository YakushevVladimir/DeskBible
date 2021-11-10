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

import com.BibleQuote.domain.controller.ILibraryController;
import com.BibleQuote.domain.controller.LibraryRepository;
import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.domain.repository.LibraryLoader;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FsLibraryController implements ILibraryController {

    private final LibraryRepository libraryRepository;
    private final LibraryLoader libraryLoader;

    public FsLibraryController(LibraryLoader libraryLoader, LibraryRepository libraryRepository) {
        this.libraryLoader = libraryLoader;
        this.libraryRepository = libraryRepository;
    }

    @Override
    public void init() {
        StaticLogger.info(this, "Init");
        Map<String, BaseModule> modules = getModules();
        if (modules.isEmpty()) {
            reloadModules();
        }
    }

    @Override
    public Map<String, BaseModule> reloadModules() {
        Map<String, BaseModule> modules = libraryLoader.loadFileModules();
        libraryRepository.replace(modules.values());
        return modules;
    }

    @Override
    public Map<String, BaseModule> getModules() {
        Map<String, BaseModule> result = new TreeMap<>();
        final List<BaseModule> modules = libraryRepository.modules();
        for (BaseModule module : modules) {
            result.put(module.getID(), module);
        }
        return result;
    }

    @Override
    public BaseModule getModuleByID(String moduleID) throws OpenModuleException {
        if (moduleID == null) {
            return null;
        }

        BaseModule module = getModules().get(moduleID);
        if (module == null) {
            throw new OpenModuleException(moduleID, null);
        }
        return module;
    }

    @Override
    public void loadModule(File file) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        StaticLogger.info(this, "Load module from " + file);
        libraryRepository.add(libraryLoader.loadModule(file));
    }
}
