package com.BibleQuote.controllers;

import com.BibleQuote.dal.CacheContext;
import com.BibleQuote.dal.repository.CacheRepository;
import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.utils.Log;

import java.util.ArrayList;

public class CacheModuleController<TModule> {
	private final String TAG = "CacheRepository";

	private CacheRepository<ArrayList<TModule>> cacheRepository;

	public CacheModuleController(CacheContext cacheContext) {
		this.cacheRepository = getCacheRepository(cacheContext);
	}

	public ArrayList<TModule> getModuleList() {
		Log.i(TAG, "Get module list");
		try {
			return cacheRepository.getData();
		} catch (FileAccessException e) {
			return new ArrayList<TModule>();
		}
	}

	public void saveModuleList(ArrayList<TModule> moduleList) {
		Log.i(TAG, "Save modules list to cache");
		try {
			cacheRepository.saveData(moduleList);
		} catch (FileAccessException e) {
			Log.e(TAG, "Can't save modules to a cache.", e);
		}
	}

	public boolean isCacheExist() {
		return cacheRepository.isCacheExist();
	}

	private CacheRepository<ArrayList<TModule>> getCacheRepository(CacheContext cacheContext) {
		if (this.cacheRepository == null) {
			this.cacheRepository = new CacheRepository<ArrayList<TModule>>(cacheContext);
		}
		return cacheRepository;
	}
}
