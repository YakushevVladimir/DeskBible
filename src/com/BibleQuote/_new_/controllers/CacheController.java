package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import android.os.AsyncTask;

import com.BibleQuote._new_.dal.CacheContext;
import com.BibleQuote._new_.models.Module;

public class CacheController {
	private final String TAG = "CacheController";
	
	private CacheContext context;

	public CacheController(CacheContext context) {
		this.context = context;
    }
	
    
	public TreeMap<String, Module> loadModules(String cacheName) {
		android.util.Log.i(TAG, "Loading modules from a file system cache.");
		ArrayList<Module> moduleList = context.loadData();
		
		TreeMap<String, Module> modules = new TreeMap<String, Module>();
		for (Module module : moduleList) {
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
		context.saveData(moduleList);
	}
	
	
	@SuppressWarnings("unchecked")
	public void saveModulesAsync(TreeMap<String, Module> modules, String cacheName) {
		new SaveModulesAsync().execute(modules);
	}
	
	
	public boolean isCacheExist(String cacheName) {
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
