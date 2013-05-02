package com.BibleQuote.utils;

import java.util.regex.Pattern;

public class StringProc {

	public static String stripTags(String xmlStr) {
		return stripTags(xmlStr, getCleanTagPattern("<(.)+?>"));
	}

	public static String stripTags(String xmlStr, String HtmlFilter) {
		return stripTags(xmlStr, getCleanTagPattern("<(?!" + HtmlFilter + ")(.)*?>"));
	}

	public static String stripTags(String xmlStr, Pattern expression) {
		return xmlStr.replaceAll(expression.pattern(), "");
	}

	public static String cleanStrongNumbers(String verse) {
		return verse.replaceAll("\\s\\d+", "");
	}

	public static String cleanVerseNumbers(String verse) {
		return verse.replaceAll("^\\d+\\s+", "");
	}

	public static String cleanVerseText(String verse) {
		return cleanStrongNumbers(cleanVerseNumbers(stripTags(verse)));

	}


	private static Pattern getCleanTagPattern(String extExpression) {
		String[] tagsExpressions = {
				"<script(.)+?</script>",
				"<style(.)+?</style>",
				"<img src=\"http(.)+?>"
		};

		StringBuilder result = new StringBuilder();
		for (String expression : tagsExpressions) {
			result.append((result.length() == 0 ? "" : "|") + "(" + expression + ")");
		}
		result.insert(0, "(");
		result.append("|(" + extExpression + "))");

		return Pattern.compile(result.toString());
	}

}
