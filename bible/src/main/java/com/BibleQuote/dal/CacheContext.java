package com.BibleQuote.dal;

import com.BibleQuote.exceptions.FileAccessException;

import java.io.*;

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
