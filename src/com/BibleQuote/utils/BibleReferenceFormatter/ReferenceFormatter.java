package com.BibleQuote.utils.BibleReferenceFormatter;

import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Module;

import java.util.TreeSet;

public abstract class ReferenceFormatter implements IBibleReferenceFormatter {
	protected Module module;
	protected Book book;
	protected String chapter;
	protected TreeSet<Integer> verses;

	public ReferenceFormatter(Module module, Book book, String chapter,
							  TreeSet<Integer> verses) {
		super();
		this.module = module;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
	}

	protected String getOnLineBibleLink() {
		return "http://b-bq.eu/"
				+ book.OSIS_ID + "/" + chapter + "_" + getVerseLink()
				+ "/" + module.ShortName;

	}

	protected String getVerseLink() {
		StringBuilder verseLink = new StringBuilder();
		Integer fromVerse = 0;
		Integer toVerse = 0;
		for (Integer verse : verses) {
			if (fromVerse == 0) {
				fromVerse = verse;
			} else if ((toVerse + 1) != verse) {
				if (verseLink.length() != 0) {
					verseLink.append(",");
				}
				if (fromVerse == toVerse) {
					verseLink.append(fromVerse);
				} else {
					verseLink.append(fromVerse + "-" + toVerse);
				}
				fromVerse = verse;
			}
			toVerse = verse;
		}
		if (verseLink.length() != 0) {
			verseLink.append(",");
		}
		if (fromVerse == toVerse) {
			verseLink.append(fromVerse);
		} else {
			verseLink.append(fromVerse + "-" + toVerse);
		}

		return verseLink.toString();
	}
}
