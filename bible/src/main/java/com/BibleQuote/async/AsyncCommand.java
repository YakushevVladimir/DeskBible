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
package com.BibleQuote.async;

import com.BibleQuote.utils.Task;

public class AsyncCommand extends Task {

	private ICommand command;
	private Exception exception;
	private Boolean isSuccess;

	public interface ICommand {
		public void execute() throws Exception;
	}

	public AsyncCommand(ICommand command, String message, Boolean isHidden) {
		super(message, isHidden);
		this.command = command;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		isSuccess = false;
		try {
			command.execute();
			isSuccess = true;
		} catch (Exception e) {
			exception = e;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	public Exception getException() {
		return exception;
	}

	public Boolean isSuccess() {
		return isSuccess;
	}
}
