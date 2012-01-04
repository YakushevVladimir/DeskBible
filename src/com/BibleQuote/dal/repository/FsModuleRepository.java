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
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.FsModule;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.DataConstants;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

public class FsModuleRepository implements IModuleRepository<String, FsModule> {

	protected final String TAG = "FsModuleRepository";
	private FsLibraryContext context;
	private CacheModuleController<FsModule> cache;
	
    public FsModuleRepository(FsLibraryContext context) {
    	this.context = context;
    	this.cache = context.getCache();
    }
    

	public Collection<FsModule> loadModules() {
		ArrayList<FsModule> moduleList = new ArrayList<FsModule>();
		
		// Load zip-compressed BQ-modules
		ArrayList<String> bqZipIniFiles = context.SearchModules(new OnlyBQZipIni());
		for (String bqZipIniFile : bqZipIniFiles) {
			String moduleDataSourceId = bqZipIniFile + File.separator + DataConstants.DEFAULT_INI_FILE_NAME;
			FsModule zipModule = new FsModule(moduleDataSourceId);
			zipModule.ShortName = zipModule.getModuleFileName();
			zipModule.setName(zipModule.ShortName);
			moduleList.add(zipModule);
		}

		// Load standard BQ-modules
		ArrayList<String> bqIniFiles = context.SearchModules(new OnlyBQIni());
		for (String moduleDataSourceId : bqIniFiles) {
			FsModule fileModule = new FsModule(moduleDataSourceId);
			fileModule.ShortName = fileModule.getModuleFileName();
			fileModule.setName(fileModule.ShortName);
			moduleList.add(fileModule);
		}
		
		//cache.saveModuleList(moduleList);

		context.moduleSet = new TreeMap<String, Module>();
		for (FsModule fsModule : moduleList) {
			context.moduleSet.put(fsModule.getID(), fsModule);
		}
		context.bookSet = new LinkedHashMap<String, Book>();

		return moduleList;
	}


	public FsModule loadModuleById(String moduleDataSourceId) {
		FsModule module = null;
		BufferedReader reader = null;
		try {
			module = new FsModule(moduleDataSourceId);
			reader = context.getModuleReader(module);
			module.defaultEncoding = context.getModuleEncoding(reader);
			reader = context.getModuleReader(module);
			
			context.fillModule(module, reader);
			
			context.moduleSet.remove(module.modulePath);
			context.moduleSet.put(module.getID(), module);
			
			cache.saveModuleList(context.getModuleList(context.moduleSet));
			
		} catch (CreateModuleErrorException e) {
			Log.e(TAG, "Can't load module by " + moduleDataSourceId);
			context.moduleSet.remove(module.modulePath);
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}		
		return module;	
	}
	
	
	public Collection<FsModule> getModules() {
		if (context.moduleSet == null && cache.isCacheExist()) {
			loadCachedModules();
		}
		return context.getModuleList(context.moduleSet);	
	}
	
	
	
	public FsModule getModuleByID(String moduleID) {
		return (FsModule)context.moduleSet.get(moduleID);
	}
	
		
	
	public void insertModule(FsModule module) {
	}

	
	
	public void deleteModule(FsModule module) {
	}

	
	
	public void updateModule(FsModule module) {
	}

	
	
	public FsModule getClosedModule() {
		for (Module module : context.moduleSet.values()) {
			if (((FsModule)module).getIsClosed()) {
				return (FsModule)module;
			}
		}
		return null;
	}

	
	private void loadCachedModules() {
		ArrayList<FsModule> moduleList = cache.getModuleList();
		context.moduleSet = new TreeMap<String, Module>();
		for (FsModule fsModule : moduleList) {
			context.moduleSet.put(fsModule.getID(), fsModule);
		}
	}
}
