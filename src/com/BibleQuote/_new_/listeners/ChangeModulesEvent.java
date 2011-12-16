package com.BibleQuote._new_.listeners;

import java.util.TreeMap;

import com.BibleQuote._new_.listeners.IChangeModulesListener.ChangeCode;
import com.BibleQuote._new_.models.Module;

public class ChangeModulesEvent {
	
	public IChangeModulesListener.ChangeCode code;
	public TreeMap<String, Module> modules;
	
	public ChangeModulesEvent(ChangeCode code, TreeMap<String, Module> modules) {
		this.code = code;
		this.modules = modules;
	}
}
