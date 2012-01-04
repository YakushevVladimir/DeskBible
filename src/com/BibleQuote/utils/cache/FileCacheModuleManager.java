package com.BibleQuote.utils.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.util.Log;

import com.BibleQuote.entity.modules.IModule;

public class FileCacheModuleManager implements ICacheModuleManager {
	
	private static final String TAG = "FileCacheModuleManager";
	private File cacheDir;
	
	public FileCacheModuleManager (File cacheDir){
		this.cacheDir = cacheDir;
	}
	
	public void save(ArrayList<IModule> modules) {
		try {
			FileOutputStream fStr = new FileOutputStream(new File(cacheDir, "library.cash"));
			ObjectOutputStream out = new ObjectOutputStream(fStr);
			out.writeObject(modules);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(TAG, "The library is stored in the cache");
	}

	@SuppressWarnings("unchecked")
	public ArrayList<IModule> load() {
		ArrayList<IModule> modules = new ArrayList<IModule>();
		try {
			FileInputStream fStr = new FileInputStream(new File(cacheDir, "library.cash"));
			ObjectInputStream out = new ObjectInputStream(fStr);
			modules = (ArrayList<IModule>) out.readObject();
			out.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.i(TAG, "The library is loaded from cache");
		return modules;
	}

	public boolean isCacheExist() {
		File cache = new File(cacheDir, "library.cash");
		return cache.exists();
	}

}
