package com.BibleQuote._new_.dal.repository;

import java.util.ArrayList;
import java.util.Collection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.BibleQuote._new_.dal.DbLibraryContext;
import com.BibleQuote._new_.models.DbModule;
import com.BibleQuote._new_.models.Module;


public class DbModuleRepository implements IModuleRepository<Long> {
    
	private SQLiteDatabase db;

    public DbModuleRepository(DbLibraryContext context)
    {
    	db = context.getDB();
    }
    
    
	@Override
	public Collection<Module> getModules() {
		ArrayList<Module> moduleList = new ArrayList<Module>();
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
	public Module getModuleById(Long moduleId) {
		Module module = null;
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
	public void insertModule(Module module) {
	}

	@Override
	public void deleteModule(Long moduleId) {
	}

    
	@Override
	public void updateModule(Module module) {
	}


}
