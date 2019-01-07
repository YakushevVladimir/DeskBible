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

import android.support.annotation.NonNull;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.logger.StaticLogger;
import com.BibleQuote.domain.repository.LibraryLoader;
import com.BibleQuote.entity.modules.BQModule;
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

public class FsLibraryLoader implements LibraryLoader<BQModule> {

    @NonNull
    private List<File> mLibraryDirs;
    @NonNull
    private BQModuleRepository mModuleRepository;

    public FsLibraryLoader(@NonNull List<File> libraryDirs, @NonNull BQModuleRepository moduleRepository) {
        this.mLibraryDirs = Collections.unmodifiableList(libraryDirs);
        this.mModuleRepository = moduleRepository;
    }

    @NonNull
    @Override
    public synchronized Map<String, BaseModule> loadFileModules() {
        StaticLogger.info(this, "Load modules info");

        final List<File> libraryDirs = prepareLibraryDirs(mLibraryDirs);
        if (libraryDirs.size() == 0) {
            StaticLogger.error(this, "Module library folder not found");
            return Collections.emptyMap();
        }

        Map<String, BaseModule> result = new TreeMap<>();

        // Load zip-compressed BQ-modules
        List<String> bqZipIniFiles = searchModules(libraryDirs, new OnlyBQZipIni());
        StaticLogger.info(this, "Load zip-modules info");
        for (String bqZipIniFile : bqZipIniFiles) {
            StaticLogger.info(this, "\t- " + bqZipIniFile);
            try {
                BaseModule module = loadFileModule(getZipDataSourceId(bqZipIniFile));
                result.put(module.getID(), module);
            } catch (OpenModuleException | BookDefinitionException | BooksDefinitionException e) {
                StaticLogger.error(this, e.getMessage(), e);
            }
        }

        // Load standard BQ-modules
        List<String> bqIniFiles = searchModules(libraryDirs, new OnlyBQIni());
        StaticLogger.info(this, "Load standard modules info");
        for (String moduleDataSourceId : bqIniFiles) {
            StaticLogger.info(this, "\t- " + moduleDataSourceId);
            try {
                BaseModule module = loadFileModule(moduleDataSourceId);
                result.put(module.getID(), module);
            } catch (OpenModuleException | BookDefinitionException | BooksDefinitionException e) {
                StaticLogger.error(this, e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    public BaseModule loadModule(String path) throws OpenModuleException, BooksDefinitionException,
            BookDefinitionException {
        if (path.endsWith("zip")) {
            path = getZipDataSourceId(path);
        }
        return loadFileModule(path);
    }

    private String getZipDataSourceId(String path) {
        return path + File.separator + "bibleqt.ini";
    }

    private BaseModule loadFileModule(String moduleDataSourceId)
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
    private List<String> searchModules(@NonNull List<File> libraryDirs, @NonNull FileFilter filter) {
        ArrayList<String> result = new ArrayList<>();
        for (File item : libraryDirs) {
            // Рекурсивная функция проходит по всем каталогам в поисках ini-файлов Цитаты
            FsUtils.searchByFilter(item, result, filter);
        }
        return result;
    }
}
