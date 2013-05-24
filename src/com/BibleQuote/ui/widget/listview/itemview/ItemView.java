/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
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
package com.BibleQuote.ui.widget.listview.itemview;

import com.BibleQuote.ui.widget.listview.ItemAdapter;
import com.BibleQuote.ui.widget.listview.item.Item;

public interface ItemView {

	/**
	 * Called by the {@link ItemAdapter} the first time the ItemView is created.
	 * This is usually a good time to keep references on sub-Views.
	 */
	void prepareItemView();

	/**
	 * Called by the {@link ItemAdapter} whenever an ItemView is displayed on
	 * screen. This may occur at the first display time or when the ItemView is
	 * reused by the ListView.
	 *
	 * @param item The {@link Item} containing date used to populate this
	 *             ItemView
	 */
	void setObject(Item item);

}
