package com.BibleQuote._new_.controllers;

import java.util.ArrayList;

import android.content.Context;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.FsBookRepository;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.Module;

public class FsChapterController {
	private final String TAG = "FsChapterController";
	private FsLibraryUnitOfWork unit;
	private FsBookRepository br;

	public FsChapterController(Context context, String libraryPath) {
		unit = new FsLibraryUnitOfWork(context, libraryPath);
		br = unit.getFsBookRepository();
    }
	
	
	public ArrayList<String> getChapterNumbers(Module module, Book book) {
		ArrayList<String> result = new ArrayList<String>();
		Book currBook = book; //br.getBookById(book.OSIS_ID);
		if (currBook != null) {
			for (int i = 0; i < currBook.ChapterQty; i++) {
				result.add(String.valueOf(i + (module.ChapterZero ? 0 : 1)));
			}
		}
		return result;
	}	
	

	public Chapter getChapter(Module module, Book book, String chapterNumber) {
		Chapter chapter = new Chapter();
		chapter.Number = chapterNumber;
		chapter.VerseNumbers.add("1");
		chapter.VerseNumbers.add("2");
		chapter.VerseNumbers.add("3");
		chapter.Text = " This is a chapter #" + chapterNumber; 
		return chapter;
	}
	
	public ArrayList<String> getChapterVerses(Module module, Book book, String chapterNumber) {
		Chapter chapter = getChapter(module, book, chapterNumber);
		return chapter.VerseNumbers;
	}
}
