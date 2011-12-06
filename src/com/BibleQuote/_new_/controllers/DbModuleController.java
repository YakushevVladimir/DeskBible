package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;

import com.BibleQuote._new_.dal.DbLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.DbModuleRepository;
import com.BibleQuote._new_.models.Module;

public class DbModuleController {
	private final String TAG = "DbModuleController";
	
	private DbLibraryUnitOfWork unit;
	private DbModuleRepository mr;
	
    public DbModuleController(Context context)
    {
    	unit = new DbLibraryUnitOfWork(context);
    	mr = unit.getDbModuleRepository();
    }
    
    
    /**
     * @return Возвращает коллекцию модулей с ключом по Module.ShortName
     */
	public TreeMap<String, Module> loadModules() {
		android.util.Log.i(TAG, "Loading modules from a DB storage.");
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		
		ArrayList<Module> moduleList = new ArrayList<Module>();
		moduleList.addAll(mr.getModules());
		for (Module module : moduleList) {
			result.put(module.ShortName, module);
		}
		
		return result;
	}
	
	
	public Module getModule(long moduleId) {
		return mr.getModuleById(moduleId);
	}
	

	private void workIsDone(int flag) {
		// if Created fireEvent(Created)
		// else if Updated fireEvent(UPdated)
	}

}
