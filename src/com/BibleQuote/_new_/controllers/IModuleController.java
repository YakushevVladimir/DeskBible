package com.BibleQuote._new_.controllers;

import java.util.TreeMap;

import com.BibleQuote._new_.models.Module;
import com.BibleQuote.exceptions.ModuleNotFoundException;

public interface IModuleController {
	
	public TreeMap<String, Module> loadModules();
	
	public TreeMap<String, Module> getModules();
	
	public Module getModuleByID(String moduleID) throws ModuleNotFoundException;
	
	public Module getClosedModule();
}
