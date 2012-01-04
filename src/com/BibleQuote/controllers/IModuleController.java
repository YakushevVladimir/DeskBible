package com.BibleQuote.controllers;

import java.util.TreeMap;

import com.BibleQuote.exceptions.ModuleNotFoundException;
import com.BibleQuote.models.Module;

public interface IModuleController {
	
	public TreeMap<String, Module> loadModules();
	
	public TreeMap<String, Module> getModules();
	
	public Module getModuleByID(String moduleID) throws ModuleNotFoundException;
	
	public Module getClosedModule();
}
