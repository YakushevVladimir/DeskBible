package com.BibleQuote._new_.dal.repository;

import java.util.Collection;

public interface IModuleRepository<TModuleId, TModule> {
    
	/*
	 * Data source related methods
	 * 
	 */
	Collection<TModule> loadModules();
	
	TModule loadModuleById(TModuleId moduleDataSourceId);
	
    void insertModule(TModule module);
    
    void deleteModule(TModule module);

    void updateModule(TModule module);
    
	
	/*
	 * Internal cache related methods
	 *
	 */
	Collection<TModule> getModules();
    
	TModule getModuleByID(String moduleID);
	
	public TModule getClosedModule();
    
}
