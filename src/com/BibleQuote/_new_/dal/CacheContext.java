package com.BibleQuote._new_.dal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.util.Log;

public class CacheContext {
	private final String TAG = "CacheContext";
	
	private File cacheDir = null;
	private String cacheName;
	
	public CacheContext(File cacheDir, String cacheName) {
		this.cacheDir = cacheDir;
		this.cacheName = cacheName;
	}

	public File getCacheDir() {
		return cacheDir;
	}
	
	public boolean isCacheExist(String cacheName) {
		File cache = new File(cacheDir, cacheName);
		return cache.exists();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T loadData() {
		T data = null;
		try {
			FileInputStream fStr = new FileInputStream(new File(cacheDir, cacheName));
			ObjectInputStream out = new ObjectInputStream(fStr);
			data = (T) out.readObject();
			out.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.i(TAG, String.format("Data are loaded from the cache %1$s%2$s", cacheDir, cacheName));
		return data;
	}	
	
	public <T> void saveData(T data) {
		try {
			FileOutputStream fStr = new FileOutputStream(new File(cacheDir, cacheName));
			ObjectOutputStream out = new ObjectOutputStream(fStr);
			out.writeObject(data);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(TAG, String.format("Data are stored in the cache %1$s%2$s", cacheDir, cacheName));
	}	
}
