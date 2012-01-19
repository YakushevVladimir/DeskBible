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
package com.BibleQuote.entity;

import java.util.HashMap;

public class ItemList extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ID";
	public static final String Name = "Name";
	public static final String DatasourceID = "DatasourceID";
	
	public ItemList(String id, String name, String datasourceID){
		super();
		super.put(ID, id);
		super.put(Name, name);
		super.put(DatasourceID, datasourceID);
	}
}
