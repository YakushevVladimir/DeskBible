package com.BibleQuote.utils.textFormatters;

import java.util.LinkedHashMap;
import java.util.Map;

public class BreakVerseBibleShareFormatter implements IShareTextFormatter {
	private LinkedHashMap<Integer, String> verses;

	public BreakVerseBibleShareFormatter(LinkedHashMap<Integer, String> verses) {
		this.verses = verses;
	}

	@Override
	public String format() {
		StringBuilder shareText = new StringBuilder();

		for (Map.Entry<Integer, String> entry : verses.entrySet()) {
			if (shareText.length() != 0) {
				shareText.append(" ");
			}
			shareText.append(String.format("%1$s %2$s", entry.getKey(),
					entry.getValue())
					+ "\r\n");
		}

		return shareText.toString();
	}

}
