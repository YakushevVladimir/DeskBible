package com.BibleQuote.exceptions;

public class BQUniversalException extends Exception {

	private static final long serialVersionUID = 5535751040905987997L;
	private String errMessage;

	public BQUniversalException(String message) {
		this.errMessage = message;
	}

	@Override
	public String getMessage() {
		return errMessage;
	}
}
