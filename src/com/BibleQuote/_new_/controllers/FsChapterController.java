package com.BibleQuote._new_.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.dal.FsLibraryUnitOfWork;
import com.BibleQuote._new_.dal.repository.IChapterRepository;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.FsBook;

public class FsChapterController implements IChapterController {
	private final String TAG = "FsChapterController";
	
	private IChapterRepository<FsBook> chapterRep;

	public FsChapterController(FsLibraryUnitOfWork unit) {
		chapterRep = unit.getChapterRepository();
    }
	
	

	@Override
	public LinkedHashMap<Integer, Chapter> loadChapters(Book book) {
		if (book == null) return null;
		android.util.Log.i(TAG, "Loading chapters from a file system storage.");
		LinkedHashMap<Integer, Chapter> result = new LinkedHashMap<Integer, Chapter>();

		ArrayList<Chapter> chapterList = new ArrayList<Chapter>();
		chapterList.addAll(chapterRep.loadChapters((FsBook)book));
		for (Chapter chapter : chapterList) {
			result.put(chapter.getNumber(), chapter);
		}
		
		return result;
	}


	@Override
	public void loadChaptersAsync(Book book) {
		// TODO Auto-generated method stub
	}

	

	@Override
	public Chapter loadChapter(Book book, Integer chapterNumber) {
		if (book == null) return null;
		android.util.Log.i(TAG, String.format("Loading a chapter %1$s from a book %2$s.", chapterNumber, book.Name));
		return chapterRep.loadChapter((FsBook)book, chapterNumber);
	}

	

	@Override
	public void loadChapterAsync(Book book, Integer chapterNumber) {
		// TODO Auto-generated method stub
	}
	
	
	
	@Override
	public Chapter getChapter(Book book, Integer chapterNumber) {
		Chapter chapter = chapterRep.getChapterByNumber((FsBook)book, chapterNumber);
		if (chapter == null) {
			chapter = chapterRep.loadChapter((FsBook)book, chapterNumber);
		}
		return chapter;
	}
	
	
	@Override
	public ArrayList<Integer> getVerseNumbers(Book book, Integer chapterNumber) {
		Chapter chapter = chapterRep.getChapterByNumber((FsBook)book, chapterNumber);
		if (chapter == null) {
			chapter = chapterRep.loadChapter((FsBook)book, chapterNumber);
		}
		return chapter.getVerseNumbers();
	}


	@Override
	public ArrayList<Chapter> getChapterList(Book book) {
		ArrayList<Chapter> chapterList = (ArrayList<Chapter>) chapterRep.getChapters((FsBook)book);
		if (chapterList.size() == 0) {
			chapterList =  (ArrayList<Chapter>) chapterRep.loadChapters((FsBook)book);
		}
		return chapterList;
	}
}
