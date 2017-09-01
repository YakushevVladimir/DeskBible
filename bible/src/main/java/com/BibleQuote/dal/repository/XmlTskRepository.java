/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: XmlTskRepository.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.dal.repository;

import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

import com.BibleQuote.domain.exceptions.BQUniversalException;
import com.BibleQuote.domain.exceptions.TskNotFoundException;
import com.BibleQuote.domain.repository.ITskRepository;
import com.BibleQuote.utils.DataConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class XmlTskRepository implements ITskRepository {

	private static final String TAG = "XmlTskRepository";

	private static final String DOCUMENT = "tsk";
	private static final String BOOK = "book";
	private static final String CHAPTER = "chapter";
	private static final String VERSE = "verse";

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
				String name;
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
					default:
						// nothing
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

		File tskDir = new File(DataConstants.getFsAppDirName());
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
