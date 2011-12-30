package com.BibleQuote._new_.controllers;

import java.util.ArrayList;

import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote.exceptions.BookNotFoundException;

public interface IChapterController {
	
	public ArrayList<Chapter> getChapterList(Book book) throws BookNotFoundException;
	
	public Chapter getChapter(Book book, Integer chapterNumber) throws BookNotFoundException;
	
	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) throws BookNotFoundException;
	
	public String getChapterHTMLView(Chapter chapter);

}
