package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.FsZipModule;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.OnlyBQZipIni;

public class FsZipModuleRepository implements IModuleRepository<String> {
	
	protected final String TAG = "FsZipModuleRepository";
	FsLibraryContext context;
	
    public FsZipModuleRepository(FsLibraryContext context)
    {
    	this.context = context;
    }
    
    
	@Override
	public Collection<Module> getModules() {
		ArrayList<Module> moduleList = new ArrayList<Module>();
		
		// Add zip-compressed BQ-modules
		ArrayList<String> bqZipIniFiles = context.SearchModules(new OnlyBQZipIni());
		for (String zipFile : bqZipIniFiles) {
			FsZipModule zipModule = (FsZipModule) getModuleById(zipFile);
			moduleList.add(zipModule);
		}
		
		return moduleList;		
	}
	

	@Override
	public Module getModuleById(String zipFile) {
		FsZipModule module = null;
		BufferedReader reader = null;
		try {
			module = new FsZipModule(zipFile);
			reader = context.getTextFileReaderFromZipArchive(module.modulePath, FsZipModule.iniFileName, module.defaultEncoding);
			module.defaultEncoding = context.getModuleEncoding(reader);
			reader = context.getTextFileReaderFromZipArchive(module.modulePath, FsZipModule.iniFileName, module.defaultEncoding);
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
	public void insertModule(Module module) {
	}

	
	@Override
	public void deleteModule(String moduleId) {
	}

	
	@Override
	public void updateModule(Module module) {
	}


}
