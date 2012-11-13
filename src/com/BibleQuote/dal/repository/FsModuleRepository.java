/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.dal.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.FsModule;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

public class FsModuleRepository implements IModuleRepository<String, FsModule> {

	private final String TAG = "FsModuleRepository";
	private FsLibraryContext context;
	private CacheModuleController<FsModule> cache;
	
    public FsModuleRepository(FsLibraryContext context) {
    	this.context = context;
    	this.cache = context.getCache();
    }
    

	public Collection<FsModule> loadFileModules() {
		ArrayList<FsModule> moduleList = new ArrayList<FsModule>();
		
		synchronized (context.moduleSet) {
			
			// Load zip-compressed BQ-modules
			ArrayList<String> bqZipIniFiles = context.SearchModules(new OnlyBQZipIni());
			for (String bqZipIniFile : bqZipIniFiles) {
				String moduleDataSourceId = bqZipIniFile + File.separator + DataConstants.DEFAULT_INI_FILE_NAME;
				Log.i(TAG, "....Add zip-modules to library from " + moduleDataSourceId);
				FsModule zipModule = new FsModule(moduleDataSourceId);
				zipModule.ShortName = zipModule.getModuleFileName();
				zipModule.setName(zipModule.ShortName);
				moduleList.add(zipModule);
			}
	
			// Load standard BQ-modules
			ArrayList<String> bqIniFiles = context.SearchModules(new OnlyBQIni());
			for (String moduleDataSourceId : bqIniFiles) {
				FsModule fileModule = new FsModule(moduleDataSourceId);
				Log.i(TAG, "....Add modules to library from " + moduleDataSourceId);
				fileModule.ShortName = fileModule.getModuleFileName();
				fileModule.setName(fileModule.ShortName);
				moduleList.add(fileModule);
			}
			
			context.moduleSet = new TreeMap<String, Module>();
			for (FsModule fsModule : moduleList) {
				context.moduleSet.put(fsModule.getDataSourceID(), fsModule);
			}
			context.bookSet = new LinkedHashMap<String, Book>();
		}
		
		return moduleList;
	}


	public FsModule loadModuleById(String moduleDatasourceID) throws OpenModuleException {
		FsModule fsModule = null;
		BufferedReader reader = null;
		synchronized (context.moduleSet) {
			String moduleID = "";
			try {
				
				// remove an old module from the module collection
				moduleID = removeModule(moduleDatasourceID);
				
				fsModule = new FsModule(moduleDatasourceID);
				reader = context.getModuleReader(fsModule);
				fsModule.defaultEncoding = context.getModuleEncoding(reader);
				reader = context.getModuleReader(fsModule);
				
				Log.i(TAG, "....Load modules from " + moduleDatasourceID);
				context.fillModule(fsModule, reader);
				moduleID = fsModule.getID();
				context.moduleSet.put(moduleID, fsModule);
				Log.i(TAG, "....Add modules to library - " + moduleID);
				
			} catch (FileAccessException e) {
				Log.i(TAG, "!!!..Error open module from " + moduleDatasourceID);
				throw new OpenModuleException(moduleID, fsModule.modulePath);
				
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
					Log.i(TAG, "...Save modules list to cache");
					cache.saveModuleList(context.getModuleList(context.moduleSet));
				} catch (IOException e) {
					e.printStackTrace(); 
				}
			}		
		}
		return fsModule;	
	}
	
	
	public Collection<FsModule> getModules() {
		synchronized (context.moduleSet) {		
			if ((context.moduleSet == null || context.moduleSet.size() == 0) && cache.isCacheExist()) {
				Log.i(TAG, "....Load modules from cache");
				loadCachedModules();
			}
			return context.getModuleList(context.moduleSet);
		}
	}
	
	
	public FsModule getModuleByID(String moduleID) {
		if (moduleID == null) {
			return null;
		}
		synchronized (context.moduleSet) {		
			return (FsModule)context.moduleSet.get(moduleID);
		}
	}
		
	
	@Override
	public FsModule getModuleByDatasourceID(String moduleDatasourceID) {
		synchronized (context.moduleSet) {		
			for (Module module : context.moduleSet.values()) {
				if ( ((FsModule)module).getDataSourceID().equalsIgnoreCase(moduleDatasourceID) ) {
					return (FsModule)module;
				}
			}
		}
		return null;
	}
	
	public void insertModule(FsModule module) {
	}

	
	public void deleteModule(FsModule module) {
	}

	
	public void updateModule(FsModule module) {
	}

	
	public FsModule getClosedModule() {
		synchronized (context.moduleSet) {
			for (Module module : context.moduleSet.values()) {
				if (((FsModule)module).getIsClosed()) {
					return (FsModule)module;
				}
			}
		}
		return null;
	}

	
	private void loadCachedModules() {
		ArrayList<FsModule> moduleList = cache.getModuleList();
		synchronized (context.moduleSet) {
			context.moduleSet = new TreeMap<String, Module>();
			for (FsModule fsModule : moduleList) {
				context.moduleSet.put(
						fsModule.getIsClosed() ? fsModule.getDataSourceID() : fsModule.getID(), 
						fsModule);
			}
		}
	}

	
	private String removeModule(String moduleDatasourceID) {
		Boolean found = false;
		String moduleID = "";
		for (Module module : context.moduleSet.values()) {
			if ( module.getDataSourceID().equalsIgnoreCase(moduleDatasourceID) ) {
				moduleID = module.getID();
				found = true;
			}
		}
		if (found) {
			context.moduleSet.remove(moduleDatasourceID);
			context.moduleSet.remove(moduleID);			
		}
		return moduleID;
	}	
}
