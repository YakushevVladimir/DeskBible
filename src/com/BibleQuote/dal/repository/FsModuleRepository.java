package com.BibleQuote.dal.repository;

import com.BibleQuote.controllers.CacheModuleController;
import com.BibleQuote.dal.FsLibraryContext;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.listeners.ChangeModulesEvent;
import com.BibleQuote.modules.FsModule;
import com.BibleQuote.modules.Module;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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
			String moduleDataSourceId = bqZipIniFile + File.separator + DataConstants.DEFAULT_INI_FILE_NAME;
			loadFileModule(moduleDataSourceId, newModuleSet);
		}

		// Load standard BQ-modules
		ArrayList<String> bqIniFiles = context.SearchModules(new OnlyBQIni());
		for (String moduleDataSourceId : bqIniFiles) {
			loadFileModule(moduleDataSourceId, newModuleSet);
		}

		context.bookSet.clear();
		context.moduleSet.clear();
		context.moduleSet.putAll(newModuleSet);
		cache.saveModuleList(context.getModuleList(context.moduleSet));

		context.eventManager.fireChangeModulesEvent(new ChangeModulesEvent(ChangeModulesEvent.ChangeCode.ModulesChanged));

		return newModuleSet;
	}

	private void loadFileModule(String moduleDataSourceId, TreeMap<String, Module> newModuleSet) {
		try {
			FsModule module = loadModuleById(moduleDataSourceId);
			newModuleSet.put(module.getID(), module);
		} catch (OpenModuleException e) {
			Log.e(TAG, "Error load module from " + moduleDataSourceId);
		}
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
		context.eventManager.fireChangeModulesEvent(new ChangeModulesEvent(ChangeModulesEvent.ChangeCode.ModulesChanged));
	}
}
