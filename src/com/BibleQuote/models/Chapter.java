package com.BibleQuote.models;

import java.util.ArrayList;

public class Chapter {
	
	private Integer number;
	
	private String text;
	
	private ArrayList<Integer> verseNumbers = new ArrayList<Integer>();
	
	private ArrayList<Verse> verseList = new ArrayList<Verse>();	// to lazy loading on demand
	
	private Book book;
	
	
	public Integer getNumber() {
		return number;
	}
	
	public String getText() {
		if (text == null && verseList.size() > 0) {
			StringBuilder buffer = new StringBuilder();
			for (Verse verse : verseList) {
				buffer.append(verse.getText());
			}
			text = buffer.toString();
		}
		return text;
	}
	
	public ArrayList<Integer> getVerseNumbers() {
		return verseNumbers;
	}

	public ArrayList<Verse> getVerseList() {
		return verseList;
	}
	
	public Chapter(Book book, Integer number, ArrayList<Integer> verseNumbers, ArrayList<Verse> verseList) {
		this.book = book;
		this.number = number;
		this.verseNumbers = verseNumbers;
		this.verseList = verseList;
	}
	
	public Book getBook() {
		return book;
	}
}
