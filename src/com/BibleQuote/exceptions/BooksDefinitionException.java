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
package com.BibleQuote.exceptions;

public class BooksDefinitionException extends Exception {

	private static final long serialVersionUID = -1652902166548627455L;
	private String moduleDatasourceID;
	private int pathNameCount;
	private int fullNameCount;
	private int shortNameCount;
	private int chapterQtyCount;
	private int booksCount;

	public BooksDefinitionException(String message,
									String moduleDatasourceID, int booksCount, int pathNameCount, int fullNameCount, int shortNameCount, int chapterQtyCount) {
		super(message);
		this.moduleDatasourceID = moduleDatasourceID;
		this.booksCount = booksCount;
		this.pathNameCount = pathNameCount;
		this.fullNameCount = fullNameCount;
		this.shortNameCount = shortNameCount;
		this.chapterQtyCount = chapterQtyCount;
	}

	public BooksDefinitionException(Exception parent) {
		super(parent);
	}

	public String getModuleDatasourceID() {
		return moduleDatasourceID;
	}

	public int getBooksCount() {
		return booksCount;
	}

	public int getPathNameCount() {
		return pathNameCount;
	}

	public int getFullNameCount() {
		return fullNameCount;
	}

	public int getShortNameCount() {
		return shortNameCount;
	}

	public int getChapterQtyCount() {
		return chapterQtyCount;
	}
}
