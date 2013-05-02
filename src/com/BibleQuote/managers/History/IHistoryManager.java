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
 *
 *  @ File Name : IHistoryManager.java
 *  @ Date : 04.03.2012
 *  @ Author : Vladimir Yakushev
 *  
 */

package com.BibleQuote.managers.History;

import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.entity.ItemList;

import java.util.LinkedList;

public interface IHistoryManager {
	public void addLink(BibleReference link);

	public void clearLinks();

	public LinkedList<ItemList> getLinks();
}
