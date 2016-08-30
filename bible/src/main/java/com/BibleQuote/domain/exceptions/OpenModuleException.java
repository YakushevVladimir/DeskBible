/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: OpenModuleException.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.domain.exceptions;

import android.content.res.Resources;

import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.domain.entity.Module;

public class OpenModuleException extends Exception {

	private static final long serialVersionUID = -941193264792260938L;
	private String moduleId;
	private String moduleDatasourceId;

	public OpenModuleException(String moduleId, String moduleDatasourceId) {
		this.moduleId = moduleId;
		this.moduleDatasourceId = moduleDatasourceId;
    }

    public OpenModuleException(Module module) {
        this.moduleId = module.getID();
        this.moduleDatasourceId = module.getDataSourceID();
    }

	public String getModuleId() {
		return moduleId;
	}

	public String getModuleDatasourceId() {
		return moduleDatasourceId;
	}

	@Override
	public String getMessage() {
		Resources resources = BibleQuoteApp.getInstance().getApplicationContext().getResources();
		return String.format(resources.getString(R.string.error_open_module), moduleId, moduleDatasourceId);
	}
}
