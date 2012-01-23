package com.BibleQuote.controllers;

import java.util.TreeMap;

import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Module;

public interface IModuleController {
	
	public TreeMap<String, Module> loadFileModules();
	
	public TreeMap<String, Module> getModules();
	
	public Module getModuleByID(String moduleID) throws OpenModuleException;
	
	public Module getModuleByDatasourceID(String moduleDatasourceID) throws OpenModuleException;
	
	public Module getClosedModule();
}
