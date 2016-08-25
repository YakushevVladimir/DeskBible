/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: BookNotFoundException.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */
package com.BibleQuote.domain.exceptions;

public class BookNotFoundException extends Exception {

	private static final long serialVersionUID = -941193264792260938L;

	private String moduleID;
	private String bookID;

	public BookNotFoundException(String moduleID, String bookID) {
		this.moduleID = moduleID;
		this.bookID = bookID;
	}

	public String getBookID() {
		return bookID;
	}

	public String getModuleID() {
		return moduleID;
	}

	@Override
	public String getMessage() {
		return String.format("Book %1$s not found in module %2$s", bookID, moduleID);
	}
}
