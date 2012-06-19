package com.BibleQuote.utils;

public class StringProc {
	
	public static String stripTags(String xmlStr, String HtmlFilter, boolean all) {
		xmlStr = xmlStr.replaceAll("<script(.)+?</script>", "")
				.replaceAll("<style(.)+?</style>", "")
				.replaceAll("<img src=\"http(.)+?>", "");
		if (HtmlFilter.equals("") || all) {
			xmlStr = xmlStr.replaceAll("<(.)+?>", "");
		} else {
			xmlStr = xmlStr.replaceAll("<(?!" + HtmlFilter + ")(.)*?>", "");
		}
		return xmlStr;
	}

	public static String cleanVerseText(String verse) {
		return stripTags(verse, "", true)
				.replaceAll("^\\d+\\s+", "")
				.replaceAll("\\s\\d+", "");
	}
}
