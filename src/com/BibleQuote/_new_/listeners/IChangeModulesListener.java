package com.BibleQuote._new_.listeners;

public interface IChangeModulesListener {
	public enum ChangeCode {
		ModulesLoaded,
		ModulesAdded,
		ModulesChanged,
		ModulesDeleted
	}
	
	public void onChangeModules(ChangeModulesEvent event);
}
