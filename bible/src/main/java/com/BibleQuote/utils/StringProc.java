/*
 * Copyright (c) 2011-2015 Scripture Software
 * http://www.scripturesoftware.org
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
		return verse.replaceAll("(G|H)+?\\d+", "").replaceAll("\\s\\d+", "");
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
			result.append(result.length() == 0 ? "" : "|").append("(").append(expression).append(")");
		}
		result.insert(0, "(");
		result.append("|(").append(extExpression).append("))");

		return Pattern.compile(result.toString());
	}
}
