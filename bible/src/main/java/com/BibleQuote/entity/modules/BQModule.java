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
 * File: BQModule.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.entity.modules;

import com.BibleQuote.domain.entity.BaseModule;

import java.io.File;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 */
public class BQModule extends BaseModule {

	private static final long serialVersionUID = -660821372799486761L;
	/**
	 * modulePath is a directory path or an archive path with a name
	 */
	public final String modulePath;
	/**
	 * Имя ini-файла (раскладка в названии файла может быть произвольной)
	 */
	public final String iniFileName;
	private final Boolean isArchive;

	public BQModule(String modulePath, String iniFilename) {
		this.modulePath = modulePath;
		this.iniFileName = iniFilename;
		this.isArchive = this.modulePath.toLowerCase().endsWith(".zip");
	}

	public String getModulePath() {
		return modulePath;
	}

	@Override
	public String getDataSourceID() {
		return this.modulePath + File.separator + this.iniFileName;
	}

	@Override
	public String getID() {
		return getShortName().toUpperCase();
	}

	public Boolean isArchive() {
		return isArchive;
	}

}
