package com.BibleQuote._new_.controllers;

import java.util.ArrayList;

import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;

public interface IChapterController {
	
	public ArrayList<Chapter> getChapterList(Book book);
	
	public Chapter getChapter(Book book, Integer chapterNumber);
	
	
	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber);
	
	public String getChapterHTMLView(Chapter chapter);
	

}
