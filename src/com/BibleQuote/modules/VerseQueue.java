package com.BibleQuote.modules;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 29.06.13
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */

public class VerseQueue extends Verse {

	private Integer chapter;
	private Integer sequenceFlags;

	public final static int SEQ_NORMAL = 1;
	public final static int SEQ_REPEATED = 2;
	public final static int SEQ_SEQUENCED = 4;

	public VerseQueue(Integer ChapterNumber, Integer VerseNumber, String VerseText, Integer sequence) {
		super(VerseNumber, VerseText);

		this.chapter = ChapterNumber;
		this.sequenceFlags = sequence;
	}

	public Integer getChapter() {
		return chapter;
	}

	public Integer getSequenceFlags() {
		return sequenceFlags;
	}
}
