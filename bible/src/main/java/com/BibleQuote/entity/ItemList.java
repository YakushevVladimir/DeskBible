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

import java.io.Serializable;
import java.util.HashMap;

public class ItemList extends HashMap<String, String> implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ID";
	public static final String Name = "Name";

	public ItemList(String id, String name) {
		super();
		super.put(ID, id);
		super.put(Name, name);
	}
}
