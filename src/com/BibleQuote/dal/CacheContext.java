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

package com.BibleQuote.dal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.BibleQuote.exceptions.FileAccessException;

public class CacheContext {
	//private final String TAG = "CacheContext";
	
	private File cacheDir = null;
	private String cacheName;
	
	public CacheContext(File cacheDir, String cacheName) {
		this.cacheDir = cacheDir;
		this.cacheName = cacheName;
	}

	public File getCacheDir() {
		return cacheDir;
	}
	
	public boolean isCacheExist() {
		File cache = new File(cacheDir, cacheName);
		return cache.exists();
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> T loadData() throws FileAccessException {
		T data = null;
		try {
			FileInputStream fStr = new FileInputStream(new File(cacheDir, cacheName));
			ObjectInputStream out = new ObjectInputStream(fStr);
			data = (T) out.readObject();
			out.close();
		} catch (ClassNotFoundException e) {
			String message = String.format("Unexpected data format in the cache %1$s%2$s: $3$s",
					cacheDir, cacheName, e.getMessage());
			throw new FileAccessException(message);			
		} catch (IOException e) {
			String message = String.format("Data isn't loaded from the cache %1$s%2$s: $3$s",
					cacheDir, cacheName, e.getMessage());
			throw new FileAccessException(message);	
		}

		return data;
	}	
	
	public synchronized <T> void saveData(T data) throws FileAccessException {
		try {
			FileOutputStream fStr = new FileOutputStream(new File(cacheDir, cacheName));
			ObjectOutputStream out = new ObjectOutputStream(fStr);
			out.writeObject(data);
			out.close();
		} catch (IOException e) {
			String message = String.format("Data isn't stored in the cache %1$s%2$s: $3$s", 
					cacheDir, cacheName, e.getMessage());
			throw new FileAccessException(message);
		}
	}	
}
