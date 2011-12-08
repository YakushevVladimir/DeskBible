package com.BibleQuote._new_.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import android.os.AsyncTask;

import com.BibleQuote._new_.dal.CacheContext;
import com.BibleQuote._new_.managers.EventManager;
import com.BibleQuote._new_.models.Module;

public class CacheController {
	private final String TAG = "CacheController";
	
	private File cacheDir;
	private String cacheName;
	//private EventManager em;

	public CacheController(File cacheDir, String cacheName, EventManager eventManager) {
		this.cacheDir = cacheDir;
		this.cacheName = cacheName;
		// TODO: Выяснить зачем CacheController имеет поле EventManager
		//this.em = eventManager;
    }
    
	public TreeMap<String, Module> loadModules(String cacheName) {
		android.util.Log.i(TAG, "Loading modules from a file system cache.");
		TreeMap<String, Module> modules = new TreeMap<String, Module>();

		CacheContext context = new CacheContext(cacheDir, cacheName);
		ArrayList<Module> moduleList = context.loadData();
		Iterator<Module> it = moduleList.iterator();
		while(it.hasNext()) {
			Module module = it.next();
			modules.put(module.ShortName, module);
		}

		return modules;
	}
	
	public void saveModules(TreeMap<String, Module> modules) {
		android.util.Log.i(TAG, "Save modules to a file system cache.");
		ArrayList<Module> moduleList = new ArrayList<Module>();
		for (Module module : modules.values()) {
			moduleList.add(module);
		}
		CacheContext context = new CacheContext(cacheDir, cacheName);
		context.saveData(moduleList);
	}
	
	@SuppressWarnings("unchecked")
	public void saveModulesAsync(TreeMap<String, Module> modules, String cacheName) {
		new SaveModulesAsync().execute(modules);
	}
	
	
	public boolean isCacheExist(String cacheName) {
		CacheContext context = new CacheContext(cacheDir, cacheName);
		return context.isCacheExist(cacheName);
	}
	
	
	private class SaveModulesAsync extends AsyncTask<TreeMap<String, Module>, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(TreeMap<String, Module>... params) {
			TreeMap<String, Module> modules = params[0];
			saveModules(modules);
			return true;
		}
	}
}
