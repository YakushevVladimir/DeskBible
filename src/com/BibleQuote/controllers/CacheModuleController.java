package com.BibleQuote.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import android.util.Log;

import com.BibleQuote.dal.CacheContext;
import com.BibleQuote.dal.repository.CacheRepository;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.models.Module;

public class CacheModuleController<TModule> {
	private final String TAG = "CacheRepository";
	
	private CacheRepository<ArrayList<TModule>> cacheRepository; 

	public CacheModuleController(CacheContext cacheContext) {
		this.cacheRepository = getCacheRepository(cacheContext);
	}
	
    
	public ArrayList<TModule> getModuleList() {
		Log.i(TAG, "getModuleList()");
		try {
			return cacheRepository.getData();
		} catch (FileAccessException e) {
			return new ArrayList<TModule>(); 
		}
	}
	
	
	public TreeMap<String, TModule> getModules() {
		ArrayList<TModule> moduleList = getModuleList();
		
		TreeMap<String, TModule> modules = new TreeMap<String, TModule>();
		for (TModule module : moduleList) {
			modules.put(((Module)module).getID(), module);
		}

		return modules;
	}
	
	
	public void saveModuleList(ArrayList<TModule> moduleList) {
		Log.i(TAG, "saveModuleList()");
		try {
			cacheRepository.saveData(moduleList);
		} catch (FileAccessException e) {
			Log.e(TAG, "Can't save modules to a cache.", e);
		}
	}
	
	
	public void saveModules(TreeMap<String, TModule> modules) {
		android.util.Log.i(TAG, "Save modules to a cache.");
		
		ArrayList<TModule> moduleList = new ArrayList<TModule>();
		for (TModule module : modules.values()) {
			moduleList.add(module);
		}
		saveModuleList(moduleList);
	}
	
	
	public boolean isCacheExist() {
		return cacheRepository.getCacheContext().isCacheExist();
	}
	

	private CacheRepository<ArrayList<TModule>> getCacheRepository(CacheContext cacheContext) {
        if (this.cacheRepository == null)
        {
			this.cacheRepository = new CacheRepository<ArrayList<TModule>>(cacheContext);
        }
        return cacheRepository;
	}
}
