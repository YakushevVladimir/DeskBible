package com.BibleQuote.exceptions;

public class TskNotFoundException extends Exception {

	private static final long serialVersionUID = 5535751040905987997L;

	@Override
	public String getMessage() {
		return "TSK cross-reference library not found";
	}
}
