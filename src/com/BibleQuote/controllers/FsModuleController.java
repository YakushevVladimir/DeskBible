package com.BibleQuote.controllers;

import com.BibleQuote.dal.FsLibraryUnitOfWork;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.modules.FsModule;
import com.BibleQuote.modules.Module;

import java.util.TreeMap;

public class FsModuleController implements IModuleController {
	//private final String TAG = "FsModuleController";
	
	private IModuleRepository<String, FsModule> mRepository;

	public FsModuleController(FsLibraryUnitOfWork unit) {
		mRepository = unit.getModuleRepository();
    }
	
	@Override
	public TreeMap<String, Module> loadFileModules() {
        return mRepository.loadFileModules();
	}
	
	@Override
	public TreeMap<String, Module> getModules() {
        TreeMap<String, Module> result = mRepository.getModules();
		if (result.size() == 0) {
			return loadFileModules();
		} else {
			return result;
		}
	}
	
	@Override
	public Module getModuleByID(String moduleID) throws OpenModuleException {
		FsModule fsModule = mRepository.getModuleByID(moduleID);
		if (fsModule == null) {
			throw new OpenModuleException(moduleID, moduleID);
		}
		return 	fsModule;		
	}
}
