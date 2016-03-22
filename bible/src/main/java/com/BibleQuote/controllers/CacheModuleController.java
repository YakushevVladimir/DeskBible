package com.BibleQuote.controllers;

import com.BibleQuote.dal.CacheContext;
import com.BibleQuote.dal.repository.CacheRepository;
import com.BibleQuote.utils.Log;
import com.BibleQuote.exceptions.FileAccessException;

import java.util.ArrayList;

public class CacheModuleController<T> {
	private static final String TAG = "CacheRepository";

	private CacheRepository<ArrayList<T>> cacheRepository;

	public CacheModuleController(CacheContext cacheContext) {
		this.cacheRepository = getCacheRepository(cacheContext);
	}

	public ArrayList<T> getModuleList() {
		Log.i(TAG, "Get module list");
		try {
			return cacheRepository.getData();
		} catch (FileAccessException e) {
			return new ArrayList<T>();
		}
	}

	public void saveModuleList(ArrayList<T> moduleList) {
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

	private CacheRepository<ArrayList<T>> getCacheRepository(CacheContext cacheContext) {
		if (this.cacheRepository == null) {
			this.cacheRepository = new CacheRepository<ArrayList<T>>(cacheContext);
		}
		return cacheRepository;
	}
}
