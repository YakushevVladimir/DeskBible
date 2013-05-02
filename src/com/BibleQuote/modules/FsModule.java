/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.modules;

import java.io.File;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 */
public class FsModule extends Module {

	private static final long serialVersionUID = -660821372799486761L;

	/**
	 * modulePath is a directory path or an archive path with a name
	 */
	public final String modulePath;

	/**
	 * Имя ini-файла (раскладка в названии файла может быть произвольной)
	 */
	public final String iniFileName;

	public final Boolean isArchive;

	public FsModule(String iniFilePath) {
		modulePath = iniFilePath.substring(0, iniFilePath.lastIndexOf(File.separator));
		iniFileName = iniFilePath.substring(iniFilePath.lastIndexOf(File.separator) + 1);
		isArchive = modulePath.toLowerCase().endsWith(".zip");
	}

	@Override
	public String getDataSourceID() {
		return this.modulePath + File.separator + this.iniFileName;
	}

	@Override
	public String getID() {
		return ShortName.toUpperCase();
	}

}
