/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
			if (moduleID == null) {
				throw new OpenModuleException(moduleID, moduleDatasourceID);
			}
			result = getModuleByID(moduleID);
		} catch(OpenModuleException e) {
			try {
				if (moduleDatasourceID == null) {
					throw new OpenModuleException(moduleID, moduleDatasourceID);
				}				
				result = getModuleByDatasourceID(moduleDatasourceID);
			} catch(OpenModuleException ex) {
				exception = ex;
			}
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
