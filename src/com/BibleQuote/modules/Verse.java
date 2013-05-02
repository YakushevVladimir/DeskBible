package com.BibleQuote.modules;

public class Verse {
	private Integer number;
	private String text;

	public Verse(Integer number, String text) {
		this.number = number;
		this.text = text;
	}

	public Integer getNumber() {
		return number;
	}

	public String getText() {
		return text;
	}
}
