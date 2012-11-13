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

package com.BibleQuote.utils;

import java.io.File;
import java.io.FileFilter;

public class OnlyBQZipIni implements FileFilter {
	private String filter;

	public OnlyBQZipIni() {
		this.filter = ".zip";
	}

	public OnlyBQZipIni(String filter) {
		this.filter = filter;
	}

	public boolean accept(File myFile) {
		return myFile.getName().toLowerCase().endsWith(this.filter)
				|| myFile.isDirectory();
	}

	@Override
	public String toString() {
		return this.filter;
	}
}