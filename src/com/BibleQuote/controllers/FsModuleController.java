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
	
	
	@Override
	public TreeMap<String, Module> loadFileModules() {
		ArrayList<FsModule> moduleList = (ArrayList<FsModule>) mRepository.loadFileModules();
		
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		for (Module module : moduleList) {
			result.put(module.getID(), module);
		}
		
		return result;		
	}
	
	
	@Override
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
	
	
	@Override
	public Module getModuleByID(String moduleID, String moduleDatasourceID) throws OpenModuleException {
		Module result = null;
		OpenModuleException exception = null;
		try {
			result = getModuleByID(moduleID);
		} catch(OpenModuleException e) {
			exception = e;
			try {
				result = getModuleByDatasourceID(moduleDatasourceID);
				exception = null;
			} catch(OpenModuleException ex) {}
		}
		if (exception != null) {
			throw new OpenModuleException(moduleID, moduleDatasourceID);
		}
		return result;
	}
	
	
	@Override
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
	public Module getClosedModule() {
		return mRepository.getClosedModule();
	}

	
	/**
	 * Возвращает полностью загруженный модуль из коллекции по его пути к данным.
	 * Если модуль в коллекции isClosed, то производит его загрузку
	 * <br><font color='red'>Производится полная перезапись в кэш коллекции модулей
	 * при загрузке closed-модуля</font><br>
	 * @param moduleDatasourceID путь к данным модуля
	 * @return Возвращает полностью загруженный модуль
	 * @throws OpenModuleException - произошла ошибка при попытке загрузить данные closed-модуля
	 */	
	private Module getModuleByDatasourceID(String moduleDatasourceID) throws OpenModuleException {
		FsModule fsModule = mRepository.getModuleByDatasourceID(moduleDatasourceID);
		if (fsModule != null && fsModule.getIsClosed()) {
			fsModule = mRepository.loadModuleById(fsModule.getDataSourceID());
		}
		if (fsModule == null) {
			throw new OpenModuleException("", moduleDatasourceID);
		}
		return 	fsModule;		
	}	
}
