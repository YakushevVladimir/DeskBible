/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.BibleQuote.dal.repository;

import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;
import com.BibleQuote.exceptions.BQUniversalException;
import com.BibleQuote.exceptions.TskNotFoundException;
import com.BibleQuote.utils.DataConstants;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;

public class XmlTskRepository implements ITskRepository {

	final static String TAG = "XmlTskRepository";

	final static String DOCUMENT = "tsk";
	final static String BOOK = "book";
	final static String CHAPTER = "chapter";
	final static String VERSE = "verse";

	@Override
	public String getReferences(String book, String chapter, String verse) throws TskNotFoundException, BQUniversalException {

		String references = "";

		XmlPullParser parser;
		try {
			parser = getParser();
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			throw new BQUniversalException("Unsupported encoding in cross-references file! " + e.getMessage());
		} catch (XmlPullParserException e) {
			Log.e(TAG, e.toString());
			throw new BQUniversalException("Error get data in cross-references! " + e.getMessage());
		}

		try {
			int eventType = parser.getEventType();
			boolean done = false;
			boolean bookFind = false;
			boolean chapterFind = false;
			while (eventType != XmlResourceParser.END_DOCUMENT && !done) {
				String name = null;
				switch (eventType) {
					case XmlResourceParser.START_TAG:
						name = parser.getName();
						if (name.equalsIgnoreCase(BOOK)) {
							if (parser.getAttributeCount() == 0) {
								break;
							}
							String value = parser.getAttributeValue(0);
							bookFind = value.equalsIgnoreCase(book);
						} else if (name.equalsIgnoreCase(CHAPTER) && bookFind) {
							if (parser.getAttributeCount() == 0) {
								break;
							}
							String value = parser.getAttributeValue(0);
							chapterFind = value.equalsIgnoreCase(chapter);
						} else if (name.equalsIgnoreCase(VERSE) && chapterFind) {
							if (parser.getAttributeCount() == 0) {
								break;
							}
							String value = parser.getAttributeValue(0);
							if (value.equalsIgnoreCase(verse)) {
								references = parser.nextText();
								done = true;
							}
						}
						break;
					case XmlResourceParser.END_TAG:
						name = parser.getName();
						if (name.equalsIgnoreCase(DOCUMENT)) {
							done = true;
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			throw new BQUniversalException("Error read data from cross-references! " + e.getMessage());
		} catch (XmlPullParserException e) {
			Log.e(TAG, e.toString());
			throw new BQUniversalException("Error get data in cross-references! " + e.getMessage());
		}

		return references;
	}

	private XmlPullParser getParser() throws XmlPullParserException, UnsupportedEncodingException, TskNotFoundException {

		File tskDir = new File(DataConstants.FS_APP_DIR_NAME);
		File tsk = new File(tskDir, "tsk.xml");

		InputStreamReader iReader;
		try {
			iReader = new InputStreamReader(new FileInputStream(tsk), "UTF-8");
		} catch (FileNotFoundException e) {
			throw new TskNotFoundException();
		}
		BufferedReader buf = new BufferedReader(iReader);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(buf);
		return parser;
	}
}
