package com.BibleQuote.modules;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 01.07.13
 * Time: 12:32
 * To change this template use File | Settings | File Templates.
 */
public class ChapterQueueList {

	private ArrayList<ChapterQueue> chapterQueueList = new ArrayList<ChapterQueue>(10);

	public boolean add(ChapterQueue chapterQueue) {
		return chapterQueueList.add(chapterQueue);
	}

	public ChapterQueue get(Integer number) {
		return chapterQueueList.get(number);
	}

	public Integer size() {
		return chapterQueueList.size();
	}

	public boolean isEmpty() {
		boolean isEmptyList = (chapterQueueList.size() == 0);

		for (int iChapterQ = 0; iChapterQ < chapterQueueList.size(); iChapterQ++) {

			ChapterQueue chapterQueue = chapterQueueList.get(iChapterQ);
			boolean isChapterEmpty = (chapterQueue == null) || chapterQueue.isEmpty();

			isEmptyList = isEmptyList || isChapterEmpty;
		}

		return isEmptyList;
	}
}
