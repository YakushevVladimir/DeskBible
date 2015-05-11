/*
 * Copyright (c) 2011-2015 Scripture Software
 * http://www.scripturesoftware.org
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.BibleQuote.async;

import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.Task;

public class AsyncRefreshModules extends Task {

	private Librarian librarian;

	public AsyncRefreshModules(String message, Boolean isHidden, Librarian librarian) {
		super(message, isHidden);
		this.librarian = librarian;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		librarian.loadFileModules();
		return true;
	}
}
