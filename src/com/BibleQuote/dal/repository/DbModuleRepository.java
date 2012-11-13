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

package com.BibleQuote.dal.repository;

import java.util.ArrayList;
import java.util.Collection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.BibleQuote.dal.DbLibraryContext;
import com.BibleQuote.models.DbModule;


public class DbModuleRepository implements IModuleRepository<Long, DbModule> {
    
	private SQLiteDatabase db;

    public DbModuleRepository(DbLibraryContext context)
    {
    	db = context.getDB();
    }
    
    
	public Collection<DbModule> loadFileModules() {
		ArrayList<DbModule> moduleList = new ArrayList<DbModule>();
		final Cursor cursor = db.rawQuery("SELECT * FROM Module ", null);
		if (cursor.moveToNext()) {
			final long Id = cursor.getLong(0);
			DbModule module = new DbModule(Id);   
			module.ShortName = cursor.getString(1);
			moduleList.add(module);
		}
		cursor.close();
		return moduleList;	
	}
	

	public DbModule loadModuleById(Long moduleId) {
		DbModule module = null;
		final Cursor cursor = db.rawQuery("SELECT * FROM Module WHERE module_id = ? ", new String[] {"" + moduleId});
		if (cursor.moveToNext()) {
			final long id = cursor.getLong(0);
			module = new DbModule(id);   
			module.ShortName = cursor.getString(1);
		}
		cursor.close();
		return module;		
	}
	
	
	public Collection<DbModule> getModules() {
		// TODO Auto-generated method stub
		return null;
	}
	

	public DbModule getModuleByID(String moduleID) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public void insertModule(DbModule module) {
	}

	public void deleteModule(DbModule module) {
	}

    
	public void updateModule(DbModule module) {
	}


	public DbModule getClosedModule() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DbModule getModuleByDatasourceID(String modulePath) {
		// TODO Auto-generated method stub
		return null;
	}

}
