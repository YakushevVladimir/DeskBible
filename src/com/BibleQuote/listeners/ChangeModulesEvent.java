package com.BibleQuote.listeners;

import com.BibleQuote.models.Module;

import java.util.TreeMap;

public class ChangeModulesEvent {
	public static enum ChangeCode {
		ModulesAdded,
		ModulesChanged,
		ModulesDeleted
	}
	
	public ChangeCode code;
	public TreeMap<String, Module> modules;
	
	public ChangeModulesEvent(ChangeCode code, TreeMap<String, Module> modules) {
		this.code = code;
		this.modules = modules;
	}
}
