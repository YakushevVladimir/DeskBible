/*
 * Copyright (c) 2011-2015 Scripture Software
 * http://www.scripturesoftware.org
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.BibleQuote.dal.repository;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.modules.FsModule;
import com.BibleQuote.modules.Module;
import com.BibleQuote.utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class FsModuleRepository implements IModuleRepository<String, FsModule> {

	private final String TAG = "FsModuleRepository";
	private FsLibraryContext context;
	private CacheModuleController<FsModule> cache;

	public FsModuleRepository(FsLibraryContext context) {
		this.context = context;
		this.cache = context.getCache();
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
		return path + File.separator + DataConstants.DEFAULT_INI_FILE_NAME;
	}

	@Override
	public void loadModule(String path) throws OpenModuleException {
		if (path.endsWith("zip")) {
			path = getZipDataSourceId(path);
		}
		loadFileModule(path, context.moduleSet);
	}

	private void loadFileModule(String moduleDataSourceId, Map<String, Module> newModuleSet) throws OpenModuleException {
		FsModule module = loadModuleById(moduleDataSourceId);
		newModuleSet.put(module.getID(), module);
	}

	private FsModule loadModuleById(String moduleDatasourceID) throws OpenModuleException {
		FsModule fsModule = null;
		BufferedReader reader = null;
		try {
			fsModule = new FsModule(moduleDatasourceID);
			reader = context.getModuleReader(fsModule);
			fsModule.defaultEncoding = context.getModuleEncoding(reader);
			reader = context.getModuleReader(fsModule);

			Log.i(TAG, "....Load modules from " + moduleDatasourceID);
			context.fillModule(fsModule, reader);
			if (!"".equals(fsModule.fontName)) {
				//loadFont(fsModule);
				Log.i(TAG, "Skip load font");
			}
		} catch (FileAccessException e) {
			Log.i(TAG, "!!!..Error open module from " + moduleDatasourceID);
			throw new OpenModuleException(moduleDatasourceID, fsModule.modulePath);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fsModule;
	}

	private void loadFont(FsModule fsModule) {
		try {
			BufferedReader reader = fsModule.isArchive
					? FsUtils.getTextFileReaderFromZipArchive(fsModule.modulePath, fsModule.fontPath, fsModule.defaultEncoding)
					: FsUtils.getTextFileReader(fsModule.modulePath, fsModule.fontPath, fsModule.defaultEncoding);
			File fontDir = new File(DataConstants.FONT_DIR);
			if (!fontDir.exists() && !fontDir.mkdir()) {
				return;
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fontDir, fsModule.fontPath)));

			int value;
			while ((value = reader.read()) != -1) {
				writer.write(value);
			}
			reader.close();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, Module> getModules() {
		if ((context.moduleSet == null || context.moduleSet.size() == 0) && cache.isCacheExist()) {
			Log.i(TAG, "....Load modules from cache");
			loadCachedModules();
		}
		return context.moduleSet;
	}

	public FsModule getModuleByID(String moduleID) {
		if (moduleID == null) {
			return null;
		}
		return (FsModule) context.moduleSet.get(moduleID);
	}

	@Override
	public void insertModule(FsModule module) {
		context.moduleSet.put(module.getID(), module);
	}

	@Override
	public void deleteModule(String moduleID) {
		if (context.moduleSet.containsKey(moduleID)) context.moduleSet.remove(moduleID);
	}

	@Override
	public void updateModule(FsModule module) {
		deleteModule(module.getID());
		insertModule(module);
	}

	private void loadCachedModules() {
		ArrayList<FsModule> moduleList = cache.getModuleList();
		context.moduleSet = new TreeMap<String, Module>();
		for (FsModule fsModule : moduleList) {
			insertModule(fsModule);
		}
	}
}
