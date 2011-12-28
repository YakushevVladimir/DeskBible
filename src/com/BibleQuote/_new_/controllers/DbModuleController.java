package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import com.BibleQuote._new_.dal.DbLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.managers.EventManager;
import com.BibleQuote._new_.models.DbModule;
import com.BibleQuote._new_.models.Module;

public class DbModuleController implements IModuleController {
	private final String TAG = "DbModuleController";
	
	//private EventManager eventManager;
	private IModuleRepository<Long, DbModule> mRepository;
	
    public DbModuleController(DbLibraryUnitOfWork unit, EventManager eventManager)
    {
		//this.eventManager = eventManager;
		mRepository = unit.getModuleRepository();    	
    }
    
    
    /**
     * @return Возвращает коллекцию модулей с ключом по Module.ShortName
     */
	@Override
	public TreeMap<String, Module> getModules() {
		android.util.Log.i(TAG, "Loading modules from a DB storage.");
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		
		ArrayList<Module> moduleList = new ArrayList<Module>();
		moduleList.addAll(mRepository.loadModules());
		for (Module module : moduleList) {
			result.put(module.getID(), module);
		}
		
		return result;	
	}
	
	
	@Override
	public Module getModuleByID(String moduleID) {
		return mRepository.getModuleByID(moduleID);
	}


	@Override
	public Module getClosedModule() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TreeMap<String, Module> loadModules() {
		// TODO Auto-generated method stub
		return null;
	}

}
