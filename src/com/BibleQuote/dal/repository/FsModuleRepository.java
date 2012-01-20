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
import com.BibleQuote.exceptions.ModuleNotFoundException;
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
    

	public Collection<FsModule> loadModules() {
		ArrayList<FsModule> moduleList = new ArrayList<FsModule>();
		
		synchronized (context.moduleSet) {
			
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
			
			cache.saveModuleList(moduleList);
	
			context.moduleSet = new TreeMap<String, Module>();
			for (FsModule fsModule : moduleList) {
				context.moduleSet.put(fsModule.getDataSourceID(), fsModule);
			}
			context.bookSet = new LinkedHashMap<String, Book>();
		}
		
		return moduleList;
	}


	public FsModule loadModuleById(String moduleDataSourceId) throws ModuleNotFoundException {
		FsModule fsModule = null;
		BufferedReader reader = null;
		synchronized (context.moduleSet) {
			try {
				
				// DEBUG cycle
				//for (int i=0; i<10; i++) {
					
				fsModule = new FsModule(moduleDataSourceId);
				reader = context.getModuleReader(fsModule);
				fsModule.defaultEncoding = context.getModuleEncoding(reader);
				reader = context.getModuleReader(fsModule);
				
				context.fillModule(fsModule, reader);
				//}
				
				context.moduleSet.remove(fsModule.getDataSourceID());
				context.moduleSet.put(fsModule.getID(), fsModule);
				
			} catch (FileAccessException e) {
				Log.e(TAG, "Can't load module " + moduleDataSourceId, e);
				context.moduleSet.remove(fsModule.getDataSourceID());
				throw new ModuleNotFoundException(fsModule.modulePath);
				
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace(); 
				}
			}		
	
			cache.saveModuleList(context.getModuleList(context.moduleSet));
		}
		return fsModule;	
	}
	
	
	public Collection<FsModule> getModules() {
		if ((context.moduleSet == null || context.moduleSet.size() == 0) && cache.isCacheExist()) {
			loadCachedModules();
		}
		return context.getModuleList(context.moduleSet);	
	}
	
	
	public FsModule getModuleByID(String moduleID) {
		return (FsModule)context.moduleSet.get(moduleID);
	}
		
	
	@Override
	public FsModule getModuleByDatasourceID(String moduleDatasourceID) {
		for (Module module : context.moduleSet.values()) {
			if ( ((FsModule)module).getDataSourceID().equalsIgnoreCase(moduleDatasourceID) ) {
				return (FsModule)module;
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
		for (Module module : context.moduleSet.values()) {
			if (((FsModule)module).getIsClosed()) {
				return (FsModule)module;
			}
		}
		return null;
	}

	
	private void loadCachedModules() {
		ArrayList<FsModule> moduleList = cache.getModuleList();
		synchronized (context.moduleSet) {
			context.moduleSet = new TreeMap<String, Module>();
			for (FsModule fsModule : moduleList) {
				context.moduleSet.put(fsModule.getID(), fsModule);
			}
		}
	}

}
