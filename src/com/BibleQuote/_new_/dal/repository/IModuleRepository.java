package com.BibleQuote._new_.dal.repository;

import java.util.Collection;

import com.BibleQuote._new_.models.Module;

public interface IModuleRepository<T> {
    
	Collection<Module> getModules();
    
	Module getModuleById(T moduleId);
	
    void insertModule(Module module);
    
    void deleteModule(T moduleId);

    void updateModule(Module module);
    
}
