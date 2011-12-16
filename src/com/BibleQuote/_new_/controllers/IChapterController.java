package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;

public interface IChapterController {
	
	public LinkedHashMap<Integer, Chapter> loadChapters(Book book);
	
	public void loadChaptersAsync(Book book);
	
	
	public Chapter loadChapter(Book book, Integer chapterNumber);
	
	public void loadChapterAsync(Book book, Integer chapterNumber);
	
	
	public ArrayList<Chapter> getChapterList(Book book);
	
	public Chapter getChapter(Book book, Integer chapterNumber);
	
	
	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber);
}
