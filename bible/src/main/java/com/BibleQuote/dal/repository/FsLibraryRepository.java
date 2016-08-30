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
 * File: FsLibraryRepository.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository;

import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.exceptions.BookDefinitionException;
import com.BibleQuote.domain.exceptions.BooksDefinitionException;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.repository.ILibraryRepository;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.FsUtils;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class FsLibraryRepository implements ILibraryRepository<BQModule> {

    private static final String TAG = FsLibraryRepository.class.getSimpleName();

    private BQModuleRepository repository;
    private File libraryDir;

    public FsLibraryRepository(File libraryDir) {
        this.libraryDir = libraryDir;
        this.repository = new BQModuleRepository();
    }

    @Override
    public synchronized Map<String, Module> loadFileModules() {

		Log.i(TAG, "Load modules from sd-card:");

        Map<String, Module> newModuleSet = new TreeMap<String, Module>();

		// Load zip-compressed BQ-modules
        ArrayList<String> bqZipIniFiles = searchModules(new OnlyBQZipIni());
        for (String bqZipIniFile : bqZipIniFiles) {
            try {
                Module module = loadFileModule(getZipDataSourceId(bqZipIniFile));
                newModuleSet.put(module.getID(), module);
            } catch (OpenModuleException e) {
                e.printStackTrace();
            } catch (BookDefinitionException e) {
                e.printStackTrace();
            } catch (BooksDefinitionException e) {
                e.printStackTrace();
            }
        }

		// Load standard BQ-modules
        ArrayList<String> bqIniFiles = searchModules(new OnlyBQIni());
        for (String moduleDataSourceId : bqIniFiles) {
            try {
                Module module = loadFileModule(moduleDataSourceId);
                newModuleSet.put(module.getID(), module);
            } catch (OpenModuleException e) {
                e.printStackTrace();
            } catch (BookDefinitionException e) {
                e.printStackTrace();
            } catch (BooksDefinitionException e) {
                e.printStackTrace();
            }
        }

		return newModuleSet;
	}

	@Override
    public Module loadModule(String path) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        if (path.endsWith("zip")) {
            path = getZipDataSourceId(path);
		}
        return loadFileModule(path);
    }

    private String getZipDataSourceId(String path) {
        return path + File.separator + "bibleqt.ini";
    }

    private Module loadFileModule(String moduleDataSourceId)
            throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
        return repository.loadModule(moduleDataSourceId);
    }

    /**
     * Выполняет поиск папок с модулями Цитаты на внешнем носителе устройства
     *
     * @return Возвращает ArrayList со списком ini-файлов модулей
     */
    private ArrayList<String> searchModules(FileFilter filter) {
        Log.i(TAG, "searchModules()");

        ArrayList<String> iniFiles = new ArrayList<String>();
        if (!isLibraryExist()) {
            return iniFiles;
        }

        try {
            // Рекурсивная функция проходит по всем каталогам в поисках ini-файлов Цитаты
            FsUtils.searchByFilter(libraryDir, iniFiles, filter);
        } catch (Exception e) {
            Log.e(TAG, "searchModules()", e);
            return iniFiles;
        }

        return iniFiles;
    }

    private boolean isLibraryExist() {
        return libraryDir != null && libraryDir.exists();
    }
}
