package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.FsFldModule;
import com.BibleQuote._new_.models.Module;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.OnlyBQIni;

public class FsFldModuleRepository implements IModuleRepository<String> {

	protected final String TAG = "FsFldModuleRepository";
	private FsLibraryContext fsContext;
	
    public FsFldModuleRepository(FsLibraryContext fsContext)
    {
    	this.fsContext = fsContext;
    }
    
    
	@Override
	public Collection<Module> getModules() {
		ArrayList<Module> moduleList = new ArrayList<Module>();
		// Add standard BQ-modules
		ArrayList<String> bqIniFiles = fsContext.SearchModules(new OnlyBQIni());
		for (String iniFile : bqIniFiles) {
			FsFldModule fileModule = (FsFldModule) getModuleById(iniFile.substring( 0, iniFile.lastIndexOf("/") ));
			moduleList.add(fileModule);			
		}
		return moduleList;		
	}
	

	@Override
	public Module getModuleById(String moduleId) {
		FsFldModule module = null;
		BufferedReader reader = null;
		try {
			module = new FsFldModule(moduleId);
			reader = fsContext.getTextFileReader(module.moduleFullPath, module.iniFileName, module.defaultEncoding);
			fsContext.fillModule(module, reader);
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
