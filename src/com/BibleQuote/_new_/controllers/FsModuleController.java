package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;
import android.os.AsyncTask;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.FsFldModuleRepository;
import com.BibleQuote._new_.dal.repository.FsZipModuleRepository;
import com.BibleQuote._new_.listeners.ChangeLibraryEvent;
import com.BibleQuote._new_.listeners.IChangeListener;
import com.BibleQuote._new_.managers.EventManager;
import com.BibleQuote._new_.models.Module;

public class FsModuleController {
	private final String TAG = "FsModuleController";
	
	private FsLibraryUnitOfWork unit;
	private FsFldModuleRepository mr;
	private FsZipModuleRepository zmr;
	private EventManager em;

	public FsModuleController(Context context, String libraryPath, EventManager eventManager) {
		unit = new FsLibraryUnitOfWork(context, libraryPath);
		mr = unit.getFsModuleRepository();
		zmr = unit.getFsZipModuleRepository();
		em = eventManager;
    }
    
	
    /**
     * @return Возвращает коллекцию модулей с ключом по Module.ShortName
     */
	public TreeMap<String, Module> loadModules() {
		android.util.Log.i(TAG, "Loading modules from a file system storage.");
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		
		ArrayList<Module> moduleList = new ArrayList<Module>();
		moduleList.addAll(mr.getModules());
		moduleList.addAll(zmr.getModules());
		for (Module module : moduleList) {
			result.put(module.ShortName, module);
		}
		return result;
	}

	public void loadModulesAsync() {
		new LoadModulesAsync().execute(true);
	}
	
	
	private class LoadModulesAsync extends AsyncTask<Boolean, Void, TreeMap<String, Module>> {
		@Override
		protected void onPostExecute(TreeMap<String, Module> result) {
			super.onPostExecute(result);
			ChangeLibraryEvent event = new ChangeLibraryEvent();
			event.modules = result;
			event.code = IChangeListener.ChangeCode.ModulesChanged;
			em.fireChangeLibraryEvent(event);
		}

		@Override
		protected TreeMap<String, Module> doInBackground(Boolean... params) {
			return loadModules();
		}
	}	
}
