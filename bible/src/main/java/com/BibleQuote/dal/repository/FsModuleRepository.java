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
 * File: FsModuleRepository.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.dal.repository;

import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.dal.controllers.FsCacheModuleController;
import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.exceptions.OpenModuleException;
import com.BibleQuote.domain.repository.old.IModuleRepository;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class FsModuleRepository implements IModuleRepository<String, BQModule> {

	private static final String TAG = "FsModuleRepository";

	private FsLibraryContext context;
    private FsCacheModuleController<BQModule> cache;
    private BQModuleRepository repository;

	public FsModuleRepository(FsLibraryContext context) {
		this.context = context;
		this.cache = context.getCache();
        this.repository = new BQModuleRepository(context);
    }

	public synchronized Map<String, Module> loadFileModules() {

		Log.i(TAG, "Load modules from sd-card:");

		TreeMap<String, Module> newModuleSet = new TreeMap<String, Module>();

		// Load zip-compressed BQ-modules
		ArrayList<String> bqZipIniFiles = context.SearchModules(new OnlyBQZipIni());
		for (String bqZipIniFile : bqZipIniFiles) {
			try {
				loadFileModule(getZipDataSourceId(bqZipIniFile), newModuleSet);
			} catch (OpenModuleException e) {
				e.printStackTrace();
			}
		}

		// Load standard BQ-modules
		ArrayList<String> bqIniFiles = context.SearchModules(new OnlyBQIni());
		for (String moduleDataSourceId : bqIniFiles) {
			try {
				loadFileModule(moduleDataSourceId, newModuleSet);
			} catch (OpenModuleException e) {
				e.printStackTrace();
			}
		}

		context.bookSet.clear();
		context.moduleSet.clear();
		context.moduleSet.putAll(newModuleSet);
		cache.saveModuleList(context.getModuleList(context.moduleSet));

		return newModuleSet;
	}

	private String getZipDataSourceId(String path) {
        return path + File.separator + "bibleqt.ini";
    }

	@Override
	public void loadModule(String path) throws OpenModuleException {
		if (path.endsWith("zip")) {
			path = getZipDataSourceId(path);
		}
		loadFileModule(path, context.moduleSet);
	}

	private void loadFileModule(String moduleDataSourceId, Map<String, Module> newModuleSet) throws OpenModuleException {
        Module module = repository.loadModule(moduleDataSourceId);
        newModuleSet.put(module.getID(), module);
	}

	public Map<String, Module> getModules() {
		if ((context.moduleSet == null || context.moduleSet.size() == 0) && cache.isCacheExist()) {
			Log.i(TAG, "....Load modules from cache");
			loadCachedModules();
		}
		return context.moduleSet;
	}

    public BQModule getModuleByID(String moduleID) {
        if (moduleID == null) {
			return null;
		}
        return (BQModule) context.moduleSet.get(moduleID);
    }

	@Override
    public void insertModule(BQModule module) {
        context.moduleSet.put(module.getID(), module);
	}

	@Override
	public void deleteModule(String moduleID) {
		if (context.moduleSet.containsKey(moduleID)) context.moduleSet.remove(moduleID);
	}

	@Override
    public void updateModule(BQModule module) {
        deleteModule(module.getID());
		insertModule(module);
	}

	private void loadCachedModules() {
        ArrayList<BQModule> moduleList = cache.getModuleList();
        context.moduleSet = new TreeMap<String, Module>();
        for (BQModule fsModule : moduleList) {
            insertModule(fsModule);
		}
	}
}
