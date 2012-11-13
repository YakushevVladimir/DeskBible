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

/**
 * @author Yakushev Vladimir, Sergey Ursul
 * 
 */
public class DbBook extends Book {

	private static final long serialVersionUID = 2083315049799687476L;
	
	/**
	 * Идентификатор книги в БД
	 */
	public Long Id;

	/**
	 * Идентификатор модуля книги в БД
	 */
	public Long ModuleId;
	
	/**
	 * Путь к файлу с книгой в папке модуля
	 */
	public String PathName;
	

	public DbBook(Module module, String name, String pathName, String shortNames, int chapterQty, long id) {
		super(module, name, shortNames, chapterQty);
		this.Id = id;
		this.PathName = pathName;
	}


	@Override
	public Object getDataSourceID() {
		return Id;
	}


}
