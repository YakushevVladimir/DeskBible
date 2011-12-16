package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.FsModule;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote._new_.utils.DataConstants;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.OnlyBQIni;
import com.BibleQuote.utils.OnlyBQZipIni;

public class FsModuleRepository implements IModuleRepository<String, FsModule> {

	protected final String TAG = "FsFldModuleRepository";
	private FsLibraryContext context;
	private TreeMap<String, Module> moduleSet;
	
    public FsModuleRepository(FsLibraryContext context) {
    	this.context = context;
    	moduleSet = context.moduleSet;
    }
    

	@Override
	public Collection<FsModule> loadModules() {
		moduleSet = new TreeMap<String, Module>();
		// Load standard BQ-modules
		ArrayList<String> bqIniFiles = context.SearchModules(new OnlyBQIni());
		for (String bqIniFile : bqIniFiles) {
			FsModule fileModule = loadModuleById(bqIniFile);
			moduleSet.put(fileModule.ShortName, fileModule);			
		}
		
		// Load zip-compressed BQ-modules
		ArrayList<String> bqZipIniFiles = context.SearchModules(new OnlyBQZipIni());
		for (String bqZipIniFile : bqZipIniFiles) {
			FsModule zipModule = loadModuleById(bqZipIniFile + File.separator + DataConstants.DEFAULT_INI_FILE_NAME);
			moduleSet.put(zipModule.ShortName, zipModule);	
		}
		
		return context.getModuleList(moduleSet);
	}


	@Override
	public FsModule loadModuleById(String moduleDataSourceId) {
		FsModule module = null;
		BufferedReader reader = null;
		try {
			module = new FsModule(moduleDataSourceId);
			reader = context.getModuleReader(module);
			module.defaultEncoding = context.getModuleEncoding(reader);
			reader = context.getModuleReader(module);
			
			context.fillModule(module, reader);
		} catch (CreateModuleErrorException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}		
		return module;	
	}
	
	
	@Override
	public Collection<FsModule> getModules() {
		return context.getModuleList(moduleSet);	
	}
	
	
	@Override
	public FsModule getModuleByShortName(String moduleShortName) {
		return (FsModule)moduleSet.get(moduleShortName);	
	}
	
		
	@Override
	public void insertModule(FsModule module) {
	}

	
	@Override
	public void deleteModule(FsModule module) {
	}

	
	@Override
	public void updateModule(FsModule module) {
	}



}
