/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public abstract class Module implements Serializable {
	
	private static final long serialVersionUID = -499369158022814559L;
	
	private String Name = "";
	public String ShortName = "";
	public String ChapterSign = "";
	public String VerseSign = "";
	public String HtmlFilter = "";
	public boolean ChapterZero = false;
	public boolean containsStrong = false;
	public boolean isBible = false;
	public String defaultEncoding = "utf-8";
	
	public Map<String, Book> Books = new LinkedHashMap<String, Book>();	// to lazy loading on demand
	
	// private String Categories = "";
	// private String Copyright = "";
	// private boolean containsOT = false;
	// private boolean containsNT = false;
	// private boolean containsAP = false;
	
	public String toString() {
		return this.Name;
	}
	
	public abstract String getID();
	
	public abstract String getDataSourceID();
	
	public abstract String getModuleFileName();
	
	public abstract Boolean getIsClosed();
	
	public abstract void setIsClosed(Boolean value);

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	
}