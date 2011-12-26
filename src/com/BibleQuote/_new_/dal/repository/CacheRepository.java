package com.BibleQuote._new_.dal.repository;

import com.BibleQuote._new_.dal.CacheContext;

public class CacheRepository<T> {
	private final String TAG = "CacheRepository";
	
	private CacheContext cacheContext;

	public CacheRepository(CacheContext cacheContext) {
		this.cacheContext = cacheContext;
    }
	
	
	public CacheContext getCacheContext() {
		return cacheContext;
	}
    
	public T getData() {
		android.util.Log.i(TAG, "Loading data from a file system cache.");
		return cacheContext.loadData();
	}
	
	
	public void saveData(T data) {
		android.util.Log.i(TAG, "Save modules to a file system cache.");
		cacheContext.saveData(data);
	}
	

	public boolean isCacheExist() {
		return cacheContext.isCacheExist();
	}
	

}
