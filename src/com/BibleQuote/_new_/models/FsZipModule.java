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
package com.BibleQuote._new_.models;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public class FsZipModule extends Module {

	private static final long serialVersionUID = -660821372799486761L;
	
	/**
	 * modulePath is a directory path
	 */
	public final String modulePath;

	public static final String iniFileName = "bibleqt.ini";
	
	public FsZipModule(String modulePath) {
		this.modulePath = modulePath;
	}
	
	public String getID() {
		return this.modulePath;
	}
}
