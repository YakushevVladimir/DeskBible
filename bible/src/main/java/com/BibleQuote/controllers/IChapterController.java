package com.BibleQuote.controllers;

import com.BibleQuote.entity.modules.Book;
import com.BibleQuote.entity.modules.Chapter;
import com.BibleQuote.exceptions.BookNotFoundException;

import java.util.ArrayList;

public interface IChapterController {

	public ArrayList<Chapter> getChapterList(Book book) throws BookNotFoundException;

	public Chapter getChapter(Book book, Integer chapterNumber) throws BookNotFoundException;

	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) throws BookNotFoundException;

	public String getChapterHTMLView(Chapter chapter);

}
