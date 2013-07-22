package com.BibleQuote.modules;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 29.06.13
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */

public class ChapterQueue extends Chapter {

	//private ArrayDeque<VerseQueue> verses = new ArrayDeque<VerseQueue>(200);
	private LinkedList<VerseQueue> verses;


	public ChapterQueue(Book book) {
		super(book, 0, null);
		verses = new LinkedList<VerseQueue>();
	}

	@Override
	public int size() {
		return verses.size();
	}

	public boolean offer(VerseQueue verse) {
		return verses.offer(verse);
	}

	public VerseQueue poll() {
		return verses.poll();
	}

	public VerseQueue peek() {
		return verses.peek();
	}

	public boolean isEmpty() {
		return verses.isEmpty();
	}
}
