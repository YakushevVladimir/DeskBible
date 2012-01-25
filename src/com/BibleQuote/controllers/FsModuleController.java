package com.BibleQuote.controllers;

import java.util.ArrayList;
import java.util.TreeMap;

import com.BibleQuote.dal.FsLibraryUnitOfWork;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.FsModule;
import com.BibleQuote.models.Module;

public class FsModuleController implements IModuleController {
	//private final String TAG = "FsModuleController";
	
	private IModuleRepository<String, FsModule> mRepository;

	public FsModuleController(FsLibraryUnitOfWork unit) {
		mRepository = unit.getModuleRepository();
    }
	
	
	public TreeMap<String, Module> loadFileModules() {
		ArrayList<FsModule> moduleList = (ArrayList<FsModule>) mRepository.loadFileModules();
		
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		for (Module module : moduleList) {
			result.put(module.getID(), module);
		}
		
		return result;		
	}
	
	public TreeMap<String, Module> getModules() {
		ArrayList<FsModule> moduleList = (ArrayList<FsModule>) mRepository.getModules();
		if (moduleList.size() == 0) {
			return loadFileModules();
		} else {
			TreeMap<String, Module> result = new TreeMap<String, Module>();
			for (Module module : moduleList) {
				result.put(module.getID(), module);
			}			
			return result;		
		}
	}
	

	public Module getModuleByID(String moduleID) throws OpenModuleException {
		FsModule fsModule = mRepository.getModuleByID(moduleID);
		String moduleDatasourceID = "";
		if (fsModule != null && fsModule.getIsClosed()) {
			moduleDatasourceID = fsModule.getDataSourceID();
			fsModule = mRepository.loadModuleById(moduleDatasourceID);
		}
		if (fsModule == null) {
			throw new OpenModuleException(moduleID, moduleDatasourceID);
		}
		return 	fsModule;		
	}
	
	
	@Override
	public Module getModuleByDatasourceID(String moduleDatasourceID) throws OpenModuleException {
		FsModule fsModule = mRepository.getModuleByDatasourceID(moduleDatasourceID);
		if (fsModule != null && fsModule.getIsClosed()) {
			fsModule = mRepository.loadModuleById(fsModule.getDataSourceID());
		}
		if (fsModule == null) {
			throw new OpenModuleException("", moduleDatasourceID);
		}
		return 	fsModule;		
	}
	
	
	public Module getClosedModule() {
		return mRepository.getClosedModule();
	}

}
