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
 * File: FsLibraryLoader.java
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository;

import androidx.annotation.NonNull;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.domain.repository.LibraryLoader;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FsLibraryLoader implements LibraryLoader {

    @NonNull
    private final List<File> mModulesDirs;
    @NonNull
    private final BQModuleRepository mModuleRepository;

    public FsLibraryLoader(@NonNull List<File> modulesDirs, @NonNull BQModuleRepository moduleRepository) {
        this.mModulesDirs = Collections.unmodifiableList(modulesDirs);
        this.mModuleRepository = moduleRepository;
    }

    @NonNull
    @Override
    public synchronized Map<String, BaseModule> loadFileModules() {
        StaticLogger.info(this, "Load modules info");

        final List<File> libraryDirs = prepareLibraryDirs(mModulesDirs);
        if (libraryDirs.size() == 0) {
            StaticLogger.error(this, "Module library folder not found");
            return Collections.emptyMap();
        }

        Map<String, BaseModule> result = new TreeMap<>();

        // Load zip-compressed BQ-modules
        List<File> bqZipIniFiles = searchModules(libraryDirs, new OnlyBQZipIni());
        StaticLogger.info(this, "Load zip-modules info");
        for (File bqZipIniFile : bqZipIniFiles) {
            StaticLogger.info(this, "\t- " + bqZipIniFile);
            try {
                BaseModule module = loadFileModule(bqZipIniFile);
                result.put(module.getID(), module);
            } catch (OpenModuleException | BookDefinitionException | BooksDefinitionException e) {
                StaticLogger.error(this, e.getMessage(), e);
            }
        }

        // Load standard BQ-modules
        List<File> bqIniFiles = searchModules(libraryDirs, new OnlyBQIni());
        StaticLogger.info(this, "Load standard modules info");
        for (File item : bqIniFiles) {
            StaticLogger.info(this, "\t- " + item);
            try {
                BaseModule module = loadFileModule(item.getParentFile());
                result.put(module.getID(), module);
            } catch (OpenModuleException | BookDefinitionException | BooksDefinitionException e) {
                StaticLogger.error(this, e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    public BaseModule loadModule(File file) throws OpenModuleException, BooksDefinitionException,
            BookDefinitionException {
        return loadFileModule(file);
    }

    private BaseModule loadFileModule(File moduleDataSourceId)
            throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        return mModuleRepository.loadModule(moduleDataSourceId);
    }

    private List<File> prepareLibraryDirs(List<File> libraryDirs) {
        List<File> result = new ArrayList<>();
        for (File item : libraryDirs) {
            if (item.exists() || item.mkdirs()) {
                result.add(item);
            } else {
                StaticLogger.error(this, "Library directory inaccessible - " + item.getAbsolutePath());
            }
        }
        return result;
    }

    /**
     * Выполняет поиск папок с модулями Цитаты на внешнем носителе устройства
     *
     * @return Возвращает ArrayList со списком ini-файлов модулей
     */
    private List<File> searchModules(@NonNull List<File> libraryDirs, @NonNull FileFilter filter) {
        List<File> result = new ArrayList<>();
        for (File item : libraryDirs) {
            // Рекурсивная функция проходит по всем каталогам в поисках ini-файлов Цитаты
            FsUtils.searchByFilter(item, result, filter);
        }
        return result;
    }
}
