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

import com.BibleQuote.dal.DbLibraryUnitOfWork;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.EventManager;
import com.BibleQuote.models.DbModule;
import com.BibleQuote.models.Module;

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
	public TreeMap<String, Module> getModules() {
		android.util.Log.i(TAG, "Loading modules from a DB storage.");
		TreeMap<String, Module> result = new TreeMap<String, Module>();
		
		ArrayList<Module> moduleList = new ArrayList<Module>();
		moduleList.addAll(mRepository.loadFileModules());
		for (Module module : moduleList) {
			result.put(module.getID(), module);
		}
		
		return result;	
	}
	
	
	public Module getModuleByID(String moduleID) {
		return mRepository.getModuleByID(moduleID);
	}


	public Module getClosedModule() {
		// TODO Auto-generated method stub
		return null;
	}


	public TreeMap<String, Module> loadFileModules() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Module getModuleByID(String moduleID, String moduleDatasourceID)
			throws OpenModuleException {
		// TODO Auto-generated method stub
		return null;
	}

}
