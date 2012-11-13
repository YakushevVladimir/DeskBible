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

import com.BibleQuote.dal.CacheContext;
import com.BibleQuote.exceptions.FileAccessException;

public class CacheRepository<T> {
	private final String TAG = "CacheRepository";
	
	private CacheContext cacheContext;

	public CacheRepository(CacheContext cacheContext) {
		this.cacheContext = cacheContext;
    }
	
	
	public CacheContext getCacheContext() {
		return cacheContext;
	}
    
	public T getData() throws FileAccessException {
		android.util.Log.i(TAG, "Loading data from a file system cache.");
		return cacheContext.loadData();
	}
	
	
	public void saveData(T data) throws FileAccessException {
		android.util.Log.i(TAG, "Save modules to a file system cache.");
		cacheContext.saveData(data);
	}
	

	public boolean isCacheExist() {
		return cacheContext.isCacheExist();
	}
	

}
