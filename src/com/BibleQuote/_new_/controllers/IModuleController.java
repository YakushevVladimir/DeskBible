package com.BibleQuote._new_.controllers;

import java.util.TreeMap;

import com.BibleQuote._new_.models.Module;

public interface IModuleController {
	
	public TreeMap<String, Module> loadModules();
	
	public TreeMap<String, Module> getModules();
	
	public Module getModuleByID(String moduleID);
	
	public Module getClosedModule();
}
