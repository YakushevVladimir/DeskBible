
package com.BibleQuote.utils.ErrorReporter;

import android.content.Context;

public interface IErrorReporter {
	/**
	 * Sends an error report
	 * @param send error report
	 */
	public abstract void Send(String report, Context context);
}
