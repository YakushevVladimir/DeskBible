package com.BibleQuote._new_.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import android.os.AsyncTask;

import com.BibleQuote._new_.dal.CacheContext;
import com.BibleQuote._new_.managers.EventManager;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.entity.modules.IModule;

public class CacheController {
	private final String TAG = "CacheController";
	
	private File cacheDir;
	private EventManager em;

	public CacheController(File cacheDir, EventManager eventManager) {
		this.cacheDir = cacheDir;
    }
    
	public TreeMap<String, Module> loadModules(String cacheName) {
		android.util.Log.i(TAG, "Loading modules from a file system cache.");
		CacheContext context = new CacheContext(cacheDir, cacheName);
		ArrayList<Module> moduleList = context.loadData();

		TreeMap<String, Module> modules = new TreeMap<String, Module>();
		for (Module module : moduleList) {
			modules.put(module.ShortName, module);
		}
		return modules;
	}
	
	public void saveModules(TreeMap<String, Module> modules, String cacheName) {
		android.util.Log.i(TAG, "Save modules to a file system cache.");
		ArrayList<Module> moduleList = new ArrayList<Module>();
		for (Module module : modules.values()) {
			moduleList.add(module);
		}
		CacheContext context = new CacheContext(cacheDir, cacheName);
		context.saveData(moduleList);
	}
	
	public void saveModulesAsync(TreeMap<String, Module> modules, String cacheName) {
		new SaveModulesAsync().execute(true);
	}
	
	
	public boolean isCacheExist(String cacheName) {
		CacheContext context = new CacheContext(cacheDir, cacheName);
		return context.isCacheExist(cacheName);
	}
	
	
	private class SaveModulesAsync extends AsyncTask<Boolean, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(Boolean... params) {
			ArrayList<IModule> library = new ArrayList<IModule>();
			//for (IModule module : modules.values()) {
			//	library.add(module);
			//}
			//saveModules(modules, cacheName);
			return true;
		}
	}
}
