package com.BibleQuote._new_.dal.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.BibleQuote._new_.dal.FsLibraryContext;
import com.BibleQuote._new_.models.Book;
import com.BibleQuote._new_.models.Chapter;
import com.BibleQuote._new_.models.FsBook;
import com.BibleQuote._new_.models.Verse;

public class FsChapterRepository implements IChapterRepository<FsBook> {

	private FsLibraryContext context;
	
    public FsChapterRepository(FsLibraryContext context) {
    	this.context = context;
    }
    	
	
	@Override
	public Collection<Chapter> loadChapters(FsBook book) {
		if (!context.isBookLoaded(book)) {
			context.bookSet.put(book.Name, book);
		}
		
		context.chapterSet = new LinkedHashMap<Integer, Chapter>();
		
		BufferedReader reader = context.getBookReader(book);
		ArrayList<String> numbers = book.getChapterNumbers(book.getModule().ChapterZero);
		for (String chapterNumber : numbers) {
			Chapter chapter = loadChapter(book, Integer.valueOf(chapterNumber), reader);
			context.chapterSet.put(Integer.valueOf(chapterNumber), chapter);
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		return context.getChapterList(context.chapterSet); 	
	}

	
	@Override
	public Chapter loadChapter(FsBook book, Integer chapterNumber) {
		if (!context.isBookLoaded(book)) {
			context.bookSet.put(book.Name, book);
		}		
		
		BufferedReader reader = context.getBookReader(book);
		Chapter chapter = loadChapter(book, chapterNumber, reader);
		context.chapterSet.put(chapterNumber, chapter);
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		return chapter;
	}

	
	@Override
	public void insertChapter(Chapter chapter) {
	}
	

	@Override
	public void deleteChapter(Chapter chapter) {
	}
	

	@Override
	public void updateChapter(Chapter chapter) {
	}

	
	@Override
	public Collection<Chapter> getChapters(FsBook book) {
		return context.getChapterList(context.chapterSet);	
	}
	

	@Override
	public Chapter getChapterByNumber(FsBook book, Integer chapterNumber) {
		if (!context.isBookLoaded(book)) {
			context.bookSet.put(book.Name, book);
		}
		
		return context.chapterSet.get(chapterNumber);
	}


	private Chapter loadChapter(Book book, Integer chapterNumber, BufferedReader bReader)  {
		
		ArrayList<Integer> verseNumbers = new ArrayList<Integer>();
		ArrayList<Verse> verseList = new ArrayList<Verse>();
		
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String str;
			int currentChapter = book.getModule().ChapterZero ? 0 : 1;
			String chapterSign = book.getModule().ChapterSign;
			boolean chapterFind = false;
			while ((str = bReader.readLine()) != null) {
				if (str.toLowerCase().contains(chapterSign)) {
					if (chapterFind) {
						// Тег начала главы может быть не вначале строки.
						// Возьмем то, что есть до теги начала главы и добавим
						// к найденным строкам
						str = str.substring(0, str.toLowerCase().indexOf(chapterSign));
						if (str.trim().length() > 0) {
							lines.add(str);
						}
						break;
					} else if (currentChapter++ == chapterNumber) {
						chapterFind = true;
						// Тег начала главы может быть не вначале строки.
						// Обрежем все, что есть до теги начала главы и добавим
						// к найденным строкам
						str = str.substring(str.toLowerCase().indexOf(chapterSign));
					}
				}
				if (!chapterFind){
					continue;
				}
				
				lines.add(str);
			}
		} catch (IOException e) {
			return null;
		}

		String verseSign = book.getModule().VerseSign;
		int i = -1;
		for (String currLine : lines) {
			if (currLine.toLowerCase().contains(verseSign)) {
				i++;
				verseList.add(new Verse(i, currLine));
				verseNumbers.add(i);
			} else if (verseList.size() > 0) {
				verseList.set(i, new Verse(i, verseList.get(i).getText() + " " + currLine));
			}
		}

		return new Chapter(book, chapterNumber, verseNumbers, verseList);
	}		
	
}
