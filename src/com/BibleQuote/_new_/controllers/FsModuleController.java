package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IModuleRepository;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;

public class FsModuleController implements IModuleController {
	private final String TAG = "FsModuleController";
	
	private IModuleRepository<String, FsModule> mRepository;
	private CacheModuleController<FsModule> cache; 

	public FsModuleController(FsLibraryUnitOfWork unit) {
		mRepository = unit.getModuleRepository();
		cache = unit.getCacheModuleController();
    }
	
	
    /**
     * @return Возвращает коллекцию модулей с ключом по Module.ShortName
     */
	@Override
	public TreeMap<String, Module> getModules() {
		ArrayList<FsModule> moduleList = (ArrayList<FsModule>) mRepository.getModules();
		if (moduleList.size() == 0) {
			moduleList = (ArrayList<FsModule>) mRepository.loadModules();
		}
		
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		for (Module module : moduleList) {
			result.put(module.getID(), module);
		}
		
		return result;		
	}
	

	@Override
	public Module getModuleByID(String moduleID) {
		FsModule fsModule = mRepository.getModuleByID(moduleID);
		if (fsModule != null && fsModule.getIsInvalidated()) {
			fsModule = mRepository.loadModuleById(fsModule.getDataSourceID());
		}
		return 	fsModule;		
	}
	
	
	@Override
	public void invalidateModules() {
		ArrayList<FsModule> modules = (ArrayList<FsModule>) mRepository.getModules();
		for (FsModule module : modules) {
			module.setIsInvalidated(true);
		}
	}
	

	@Override
	public Module getInvalidatedModule() {
		return mRepository.getInvalidatedModule();
	}
	

}
