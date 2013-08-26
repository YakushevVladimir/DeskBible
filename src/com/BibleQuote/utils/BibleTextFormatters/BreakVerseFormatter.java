package com.BibleQuote.utils.bibleTextFormatters;

import java.util.LinkedHashMap;

public class BreakVerseFormatter implements IBibleTextFormatter {
	private LinkedHashMap<Integer, String> verses;

	public BreakVerseFormatter(LinkedHashMap<Integer, String> verses) {
		this.verses = verses;
	}

	@Override
	public String format() {
		StringBuilder shareText = new StringBuilder();

		for (Integer verseNumber : verses.keySet()) {
			if (shareText.length() != 0) {
				shareText.append(" ");
			}
			shareText.append(String.format("%1$s %2$s", verseNumber,
					verses.get(verseNumber))
					+ "\r\n");
		}

		return shareText.toString();
	}

}
