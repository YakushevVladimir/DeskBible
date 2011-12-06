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
public class FsFldModule extends Module {

	private static final long serialVersionUID = -660821372799486761L;
	private static final String iniFileName = "bibleqt.ini";
	
	/**
	 * moduleFullPath is a directory path (plus a file name for an archive module).
	 */
	public String moduleFullPath = "";

	public FsFldModule(String moduleFullPath) {
		this.moduleFullPath = moduleFullPath.charAt(moduleFullPath.length()-1) == '/'  
				? moduleFullPath				
				: moduleFullPath + "/";
	}
	
	public String getIniFullPath() {
		return this.moduleFullPath + iniFileName;
	}
}
