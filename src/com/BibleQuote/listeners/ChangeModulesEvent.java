package com.BibleQuote.listeners;

import com.BibleQuote.modules.Module;

import java.util.TreeMap;

public class ChangeModulesEvent {
	public static enum ChangeCode {
		ModulesAdded,
		ModulesChanged,
		ModulesDeleted
	}

	public ChangeCode code;
	public TreeMap<String, Module> modules = new TreeMap<String, Module>();

	public ChangeModulesEvent(ChangeCode code, TreeMap<String, Module> modules) {
		this.code = code;
		this.modules = modules;
	}

	public ChangeModulesEvent(ChangeCode code) {
		this.code = code;
	}
}
