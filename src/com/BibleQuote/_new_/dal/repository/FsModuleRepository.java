package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.BibleQuote._new_.controllers.CacheModuleController;
import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote._new_.utils.DataConstants;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

public class FsModuleRepository implements IModuleRepository<String, FsModule> {

	protected final String TAG = "FsModuleRepository";
	private FsLibraryContext context;
	private TreeMap<String, Module> moduleSet;
	private CacheModuleController<FsModule> cache;
	
    public FsModuleRepository(FsLibraryContext context) {
    	this.context = context;
    	this.moduleSet = context.moduleSet;
    	this.cache = context.getCache();
    }
    

	@Override
	public Collection<FsModule> loadModules() {
		ArrayList<FsModule> moduleList = new ArrayList<FsModule>();
		
		if (cache.isCacheExist()) {
			moduleList = cache.getModuleList();
		
		} else {
		
			// Load standard BQ-modules
			ArrayList<String> bqIniFiles = context.SearchModules(new OnlyBQIni());
			for (String moduleDataSourceId : bqIniFiles) {
				FsModule fileModule = new FsModule(moduleDataSourceId);
				fileModule.ShortName = fileModule.getModuleFileName();
				fileModule.setName(fileModule.ShortName);
				moduleList.add(fileModule);
			}
			
			// Load zip-compressed BQ-modules
			ArrayList<String> bqZipIniFiles = context.SearchModules(new OnlyBQZipIni());
			for (String bqZipIniFile : bqZipIniFiles) {
				String moduleDataSourceId = bqZipIniFile + File.separator + DataConstants.DEFAULT_INI_FILE_NAME;
				FsModule zipModule = new FsModule(moduleDataSourceId);
				zipModule.ShortName = zipModule.getModuleFileName();
				zipModule.setName(zipModule.ShortName);
				moduleList.add(zipModule);
			}
		}

		moduleSet = new TreeMap<String, Module>();
		for (FsModule fsModule : moduleList) {
			moduleSet.put(fsModule.getID(), fsModule);
		}

		context.bookSet = new LinkedHashMap<String, Book>();
		context.chapterSet = new LinkedHashMap<Integer, Chapter>();

		return moduleList;
	}


	@Override
	public FsModule loadModuleById(String moduleDataSourceId) {
		FsModule module = null;
		BufferedReader reader = null;
		try {
			module = new FsModule(moduleDataSourceId);
			reader = context.getModuleReader(module);
			module.defaultEncoding = context.getModuleEncoding(reader);
			reader = context.getModuleReader(module);
			
			context.fillModule(module, reader);
			
			moduleSet.remove(module.modulePath);
			moduleSet.put(module.getID(), module);
			
			if (getInvalidatedModule() == null) {
				cache.saveModuleList(context.getModuleList(moduleSet));
			}
			
		} catch (CreateModuleErrorException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}		
		return module;	
	}
	
	
	@Override
	public Collection<FsModule> getModules() {
		return context.getModuleList(moduleSet);	
	}
	
	
	@Override
	public FsModule getModuleByID(String moduleID) {
		return (FsModule)moduleSet.get(moduleID);
	}
	
		
	@Override
	public void insertModule(FsModule module) {
	}

	
	@Override
	public void deleteModule(FsModule module) {
	}

	
	@Override
	public void updateModule(FsModule module) {
	}

	
	@Override
	public FsModule getInvalidatedModule() {
		for (Module module : moduleSet.values()) {
			if (((FsModule)module).getIsInvalidated()) {
				return (FsModule)module;
			}
		}
		return null;
	}

}
