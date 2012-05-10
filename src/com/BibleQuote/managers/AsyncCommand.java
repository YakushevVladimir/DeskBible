package com.BibleQuote.managers;

import com.BibleQuote.utils.Task;

public class AsyncCommand extends Task {
	
	private ICommand command;
	private Exception exception;
	private Boolean isSuccess;
	
	public interface ICommand {
		public void execute();
	}
	
	public AsyncCommand(String message, Boolean isHidden, ICommand command) {
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
