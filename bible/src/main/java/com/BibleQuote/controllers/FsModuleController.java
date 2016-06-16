/*
 * Copyright (c) 2011-2015 Scripture Software
 * http://www.scripturesoftware.org
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.BibleQuote.controllers;

import com.BibleQuote.dal.LibraryUnitOfWork;
import com.BibleQuote.dal.repository.IModuleRepository;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.modules.FsModule;
import com.BibleQuote.modules.Module;

import java.util.Map;

public class FsModuleController implements IModuleController {

	private IModuleRepository<String, FsModule> mRepository;

	public FsModuleController(LibraryUnitOfWork unit) {
		mRepository = unit.getModuleRepository();
	}

	@Override
	public Map<String, Module> loadFileModules() {
		return mRepository.loadFileModules();
	}

	@Override
	public Map<String, Module> getModules() {
		Map<String, Module> result = mRepository.getModules();
		if (result.isEmpty()) {
			return loadFileModules();
		} else {
			return result;
		}
	}

	@Override
	public Module getModuleByID(String moduleID) throws OpenModuleException {
		FsModule fsModule = mRepository.getModuleByID(moduleID);
		if (fsModule == null) {
			throw new OpenModuleException(moduleID, moduleID);
		}
		return fsModule;
	}

	@Override
	public void loadModule(String path) throws OpenModuleException {
		mRepository.loadModule(path);
	}
}
