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

public class ModuleNotFoundException extends Exception {

	private static final long serialVersionUID = -941193264792260938L;

	private String moduleId;
	
	public ModuleNotFoundException(String moduleId) {
		this.moduleId = moduleId;
	}
	
	public String toString() {
		return moduleId == null 
				? String.format("Module with ID=%1$s is not found", moduleId)
				: String.format("Module is not found");
	}

}
