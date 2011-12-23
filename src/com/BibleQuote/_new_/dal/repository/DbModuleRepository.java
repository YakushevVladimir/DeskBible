package com.BibleQuote._new_.dal.repository;

import java.util.ArrayList;
import java.util.Collection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.BibleQuote._new_.dal.DbLibraryContext;
import com.BibleQuote._new_.models.DbModule;


public class DbModuleRepository implements IModuleRepository<Long, DbModule> {
    
	private SQLiteDatabase db;

    public DbModuleRepository(DbLibraryContext context)
    {
    	db = context.getDB();
    }
    
    
	@Override
	public Collection<DbModule> loadModules() {
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
	

	@Override
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
	
	
	@Override
	public Collection<DbModule> getModules() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public DbModule getModuleByID(String moduleID) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public void insertModule(DbModule module) {
	}

	@Override
	public void deleteModule(DbModule module) {
	}

    
	@Override
	public void updateModule(DbModule module) {
	}


	@Override
	public DbModule getInvalidatedModule() {
		// TODO Auto-generated method stub
		return null;
	}

}
