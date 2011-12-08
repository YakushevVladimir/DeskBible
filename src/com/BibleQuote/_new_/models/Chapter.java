package com.BibleQuote._new_.models;

import java.util.ArrayList;

public class Chapter {
	public String Number;
	public String Text;
	
	public ArrayList<String> VerseNumbers = new ArrayList<String>();
	
	public ArrayList<Verse> VerseList;	// to lazy loading on demand
}
