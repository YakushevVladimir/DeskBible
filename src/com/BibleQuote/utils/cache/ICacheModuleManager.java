package com.BibleQuote.utils.cache;

import java.util.ArrayList;

import com.BibleQuote.entity.modules.IModule;

public interface ICacheModuleManager {
	public abstract void save(ArrayList<IModule> modules);
	public abstract ArrayList<IModule> load();
	public abstract boolean isCacheExist();
}
