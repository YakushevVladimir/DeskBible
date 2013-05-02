package com.BibleQuote.utils;

import com.BibleQuote.entity.BibleBooksID;
import com.BibleQuote.entity.BibleReference;

import java.util.LinkedHashSet;

public class BibleLinkParser {


	//private static final String VERSE_SEPARATOR = ":";
	private static final String TO_VERSE_SEPARATOR = "-";
	private static final String LINK_SEPARATOR = ";";

	public static LinkedHashSet<BibleReference> parse(String moduleID, String references) {

		LinkedHashSet<BibleReference> bibleLinks = new LinkedHashSet<BibleReference>();
		String currSymbol;
		StringBuilder book, chapter, fromVerse, toVerse;

		references = references.toLowerCase().replaceAll("\\s+?", "").replaceAll("\\.", "");

		for (String currLink : references.split(LINK_SEPARATOR)) {
			currSymbol = "";
			book = new StringBuilder();
			chapter = new StringBuilder();
			fromVerse = new StringBuilder();
			toVerse = new StringBuilder();
			int currPos = 0;

			// Parse book
			while (currPos < (currLink.length())) {
				currSymbol = currLink.substring(currPos, currPos + 1);
				if (isDigit(currSymbol) && currPos != 0) {
					break;
				}
				currPos++;
				book.append(currSymbol);
			}
			if (book.length() == 0) {
				continue;
			} else {
				book = new StringBuilder(BibleBooksID.getID(book.toString()));
			}

			// Parse chapter
			while (currPos < (currLink.length())) {
				currSymbol = currLink.substring(currPos, currPos + 1);
				currPos++;
				if (!isDigit(currSymbol)) {
					break;
				}
				chapter.append(currSymbol);
			}
			if (chapter.length() == 0) {
				continue;
			}

			// Parse fromVerse
			while (currPos < (currLink.length())) {
				currSymbol = currLink.substring(currPos, currPos + 1);
				currPos++;
				if (!isDigit(currSymbol)) {
					break;
				}
				fromVerse.append(currSymbol);
			}
			if (fromVerse.length() == 0) {
				continue;
			}

			// Parse toVerse
			if (!currSymbol.equals(TO_VERSE_SEPARATOR)) {
				toVerse = fromVerse;
			} else {
				while (currPos < (currLink.length())) {
					currSymbol = currLink.substring(currPos, currPos + 1);
					currPos++;
					if (!isDigit(currSymbol)) {
						break;
					}
					toVerse.append(currSymbol);
				}
				if (toVerse.length() == 0) {
					toVerse = fromVerse;
				}
			}

			bibleLinks.add(new BibleReference(
					moduleID,
					book.toString(),
					Integer.parseInt(chapter.toString()),
					Integer.parseInt(fromVerse.toString()),
					Integer.parseInt(toVerse.toString())));
		}

		return bibleLinks;
	}

	public static boolean isDigit(String symbol) {
		return "0123456789".contains(symbol);
	}

}
