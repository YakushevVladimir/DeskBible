package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import android.os.AsyncTask;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.listeners.ChangeModulesEvent;
import com.BibleQuote._new_.listeners.IChangeModulesListener.ChangeCode;
import com.BibleQuote._new_.managers.EventManager;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;

public class FsModuleController implements IModuleController {
	private final String TAG = "FsModuleController";
	
	private EventManager eventManager;
	private IModuleRepository<String, FsModule> mRepository;

	public FsModuleController(FsLibraryUnitOfWork unit, EventManager eventManager) {
		this.eventManager = eventManager;
		mRepository = unit.getModuleRepository();
    }
	
	
    /**
     * @return Возвращает коллекцию модулей с ключом по Module.ShortName
     */
	public TreeMap<String, Module> loadModules() {
		android.util.Log.i(TAG, "Loading modules from a file system storage.");
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		
		ArrayList<Module> moduleList = new ArrayList<Module>();
		moduleList.addAll(mRepository.loadModules());
		for (Module module : moduleList) {
			result.put(module.ShortName, module);
		}
		
		return result;
	}

	
	public void loadModulesAsync() {
		new LoadModulesAsync().execute(true);
	}
	
	
	public TreeMap<String, Module> getModules() {
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		
		ArrayList<Module> moduleList = new ArrayList<Module>();
		moduleList.addAll(mRepository.getModules());
		for (Module module : moduleList) {
			result.put(module.ShortName, module);
		}
		
		return result;		
	}
	
	
	private class LoadModulesAsync extends AsyncTask<Boolean, Void, TreeMap<String, Module>> {
		@Override
		protected void onPostExecute(TreeMap<String, Module> result) {
			super.onPostExecute(result);
			
			ChangeModulesEvent event = new ChangeModulesEvent(ChangeCode.ModulesLoaded, result);
			eventManager.fireChangeModulesEvent(event);
		}

		@Override
		protected TreeMap<String, Module> doInBackground(Boolean... params) {
			return loadModules();
		}
	}	
}
