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

public class BookDefinitionException extends Exception {

	private static final long serialVersionUID = -1652902166548627455L;
	private String moduleDatasourceID;
	private int bookNumber;
	private String pathName;
	private String fullName;
	private String shortName;
	private Integer chapterQty;

	public BookDefinitionException(String message,
								   String moduleDatasourceID, int bookNumber, String pathName, String fullName, String shortName, Integer chapterQty) {
		super(message);
		this.moduleDatasourceID = moduleDatasourceID;
		this.bookNumber = bookNumber;
		this.pathName = pathName;
		this.fullName = fullName;
		this.shortName = shortName;
		this.chapterQty = chapterQty;
	}

	public BookDefinitionException(Exception parent) {
		super(parent);
	}

	public String getModuleDatasourceID() {
		return moduleDatasourceID;
	}

	public int getBookNumber() {
		return bookNumber;
	}

	public String getPathName() {
		return pathName;
	}

	public String getFullName() {
		return fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public Integer getChapterQty() {
		return chapterQty;
	}

}
