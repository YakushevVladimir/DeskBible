package com.BibleQuote.controllers;

import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Chapter;

import java.util.ArrayList;

public interface IChapterController {

	public ArrayList<Chapter> getChapterList(Book book) throws BookNotFoundException;

	public Chapter getChapter(Book book, Integer chapterNumber) throws BookNotFoundException;

	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) throws BookNotFoundException;

	public String getChapterHTMLView(Chapter chapter);

	public String getParChapterHTMLView(Chapter chapter, Chapter ParChapter);

}
