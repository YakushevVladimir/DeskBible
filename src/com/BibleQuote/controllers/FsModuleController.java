package com.BibleQuote.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import com.BibleQuote.dal.FsLibraryUnitOfWork;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.models.FsModule;
import com.BibleQuote.models.Module;

public class FsModuleController implements IModuleController {
	//private final String TAG = "FsModuleController";
	
	private IModuleRepository<String, FsModule> mRepository;

	public FsModuleController(FsLibraryUnitOfWork unit) {
		mRepository = unit.getModuleRepository();
    }
	
	
	public TreeMap<String, Module> loadModules() {
		ArrayList<FsModule> moduleList = (ArrayList<FsModule>) mRepository.loadModules();
		
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		for (Module module : moduleList) {
			result.put(module.getID(), module);
		}
		
		return result;		
	}
	
    /**
     * @return Возвращает коллекцию модулей с ключом по Module.ShortName
     */
	public TreeMap<String, Module> getModules() {
		ArrayList<FsModule> moduleList = (ArrayList<FsModule>) mRepository.getModules();
		if (moduleList.size() == 0) {
			return loadModules();
		} else {
			TreeMap<String, Module> result = new TreeMap<String, Module>();
			for (Module module : moduleList) {
				result.put(module.getID(), module);
			}			
			return result;		
		}
	}
	

	public Module getModuleByID(String moduleID) throws ModuleNotFoundException {
		FsModule fsModule = mRepository.getModuleByID(moduleID);
		if (fsModule != null && fsModule.getIsClosed()) {
			fsModule = mRepository.loadModuleById(fsModule.getDataSourceID());
		}
		if (fsModule == null) {
			throw new ModuleNotFoundException(moduleID);
		}
		return 	fsModule;		
	}
	

	public Module getClosedModule() {
		return mRepository.getClosedModule();
	}


}
