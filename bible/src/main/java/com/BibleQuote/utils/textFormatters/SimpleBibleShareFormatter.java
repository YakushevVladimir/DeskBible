package com.BibleQuote.utils.textFormatters;

import java.util.LinkedHashMap;

public class SimpleBibleShareFormatter implements IShareTextFormatter {
	private LinkedHashMap<Integer, String> verses;

	public SimpleBibleShareFormatter(LinkedHashMap<Integer, String> verses) {
		this.verses = verses;
	}

	@Override
	public String format() {
		StringBuilder shareText = new StringBuilder();

		Integer prevVerseNumber = 0;
		for (Integer verseNumber : verses.keySet()) {
			if (prevVerseNumber == 0) {
				prevVerseNumber = verseNumber;
			}

			if (verseNumber - prevVerseNumber > 1) {
				shareText.append(" ... ");
			} else if (shareText.length() != 0) {
				shareText.append(" ");
			}
			shareText.append(verses.get(verseNumber).trim());
			prevVerseNumber = verseNumber;
		}

		return shareText.toString();
	}

}
