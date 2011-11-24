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
package com.BibleQuote.entity.modules.bq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;

import com.BibleQuote.entity.Book;
import com.BibleQuote.exceptions.CreateModuleErrorException;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.StringProc;

/**
 * @author Yakushev Vladimir
 * 
 */
public class ZipFileModule extends Module {

	private final String TAG = "ZipFileModule";
	private Context context;
	private int rawID;

	public ZipFileModule(Context context, int rawID) throws CreateModuleErrorException {
		Log.i(TAG, "ZipFileModule()");
		
		this.context = context;
		this.rawID = rawID;
		
		defaultEncoding = "utf-8";

		BufferedReader bReader = getReader("bibleqt.ini");
		if (bReader == null) {
			throw new CreateModuleErrorException();
		}

		fillParameters(bReader);
	}
	
	/**
	 * @param bReader
	 * @throws CreateModuleErrorException
	 */
	protected void fillParameters(BufferedReader bReader) throws CreateModuleErrorException {
		String str, HTMLFilter = "", key, value;
		ArrayList<String> fullNames = new ArrayList<String>();
		ArrayList<String> pathNames = new ArrayList<String>();
		ArrayList<String> shortNames = new ArrayList<String>();
		ArrayList<Integer> chapterQty = new ArrayList<Integer>();

		int pos;
		try {
			while ((str = bReader.readLine()) != null) {
				pos = str.indexOf("//");
				if (pos >= 0)
					str = str.substring(0, pos);

				int delimiterPos = str.indexOf("=");
				if (delimiterPos == -1) {
					continue;
				}

				key = str.substring(0, delimiterPos).trim().toLowerCase();
				delimiterPos++;
				value = delimiterPos >= str.length() ? "" : str.substring(
						delimiterPos, str.length()).trim();

				if (key.equals("biblename")) {
					Name = value;
				} else if (key.equals("bibleshortname")) {
					ShortName = value.replaceAll("\\.", "");
				} else if (key.equals("chaptersign")) {
					ChapterSign = value.toLowerCase();
				} else if (key.equals("chapterzero")) {
					ChapterZero = value.toLowerCase().contains("y") ? true
							: false;
				} else if (key.equals("versesign")) {
					VerseSign = value.toLowerCase();
				} else if (key.equals("htmlfilter")) {
					HTMLFilter = value;
				} else if (key.equals("bible")) {
					isBible = value.toLowerCase().contains("y") ? true : false;
				} else if (key.equals("strongnumbers")) {
					this.containsStrong = value.toLowerCase().contains("y") ? true
							: false;
				} else if (key.equals("pathname")) {
					pathNames.add(value);
				} else if (key.equals("fullname")) {
					fullNames.add(value);
				} else if (key.equals("shortname")) {
					shortNames.add(value.replaceAll("\\.", ""));
				} else if (key.equals("chapterqty")) {
					try {
						chapterQty.add(Integer.valueOf(value));
					} catch (NumberFormatException e) {
						chapterQty.add(0);
					}
				}
			}
			bReader.close();
		} catch (IOException e) {
			Log.e(TAG, e);
		}

		for (int i = 0; i < fullNames.size(); i++) {
			if (pathNames.size() < i || chapterQty.size() < i) {
				break;
			} else if (fullNames.get(i).equals("")
					|| pathNames.get(i).equals("") || chapterQty.get(i) == 0) {
				// Имя книги, путь к книге и кол-во глав должны быть обязательно
				// указаны
				continue;
			}
			Book book = new Book(fullNames.get(i), pathNames.get(i), (shortNames
							.size() > i ? shortNames.get(i) : ""), chapterQty
							.get(i));
			Books.put(book.getBookID(), book);
		}
		if (Books.size() == 0) {
			throw new CreateModuleErrorException();
		}

		String TagFilter[] = { "p", "b", "i", "em", "strong", "q", "big",
				"sub", "sup", "h1", "h2", "h3", "h4" };
		ArrayList<String> TagArray = new ArrayList<String>();
		for (String tag : TagFilter) {
			TagArray.add(tag);
		}

		if (!HTMLFilter.equals("")) {
			String[] words = HTMLFilter.replaceAll("\\W", " ").trim()
					.split("\\s+");
			for (String word : words) {
				if (word.equals("") || TagArray.contains(word)) {
					continue;
				}
				TagArray.add(word);
			}
		}

		String separator = "";
		for (String tag : TagArray) {
			XFilter += separator + "(" + tag + ")|(/" + tag + ")" + "|("
					+ tag.toUpperCase() + ")|(/" + tag.toUpperCase() + ")";
			separator = "|";
		}
	}
	
	protected BufferedReader getReader(String file) {
		try {
			InputStream moduleStream = context.getResources().openRawResource(rawID);
			ZipInputStream zStream = new ZipInputStream(moduleStream);
			ZipEntry entry;
			while ((entry = zStream.getNextEntry()) != null) {
				if (entry.getName().equals(file)) {
					InputStreamReader iReader = new InputStreamReader(zStream, defaultEncoding);
					return new BufferedReader(iReader);
				};
			}
			return null;
		} catch (IOException e) {
			Log.e(TAG, e);
			return null;
		}
	}

	@Override
	public ArrayList<String> getChapterVerses(Book book, Integer chapterToView) {
		ArrayList<String> verses = new ArrayList<String>();

		BufferedReader bReader = this.getReader(book.getPath());
		if (bReader == null) {
			return verses;
		}

		ArrayList<String> lines = new ArrayList<String>();
		try {
			String str;
			int currentChapter = this.ChapterZero ? 0 : 1;
			boolean chapterFind = false;
			while ((str = bReader.readLine()) != null) {
				if (str.toLowerCase().contains(ChapterSign)) {
					if (chapterFind) {
						// Тег начала главы может быть не в начале строки.
						// Возьмем то, что есть до теги начала главы и добавим
						// к найденным строкам
						str = str.substring(0, str.toLowerCase().indexOf(ChapterSign));
						if (str.trim().length() > 0) {
							lines.add(str);
						}
						break;
					} else if (currentChapter++ == chapterToView) {
						chapterFind = true;
						// Тег начала главы может быть не в начале строки.
						// Обрежем все, что есть до теги начала главы и добавим
						// к найденным строкам
						str = str.substring(str.toLowerCase().indexOf(ChapterSign));
					}
				}
				if (!chapterFind){
					continue;
				}
				
				lines.add(str);
			}
			bReader.close();
		} catch (IOException e) {
			return verses;
		}

		int i = -1;
		for (String currLine : lines) {
			if (currLine.toLowerCase().contains(this.VerseSign)) {
				verses.add(currLine);
				i++;
			} else if (verses.size() > 0) {
				verses.set(i, verses.get(i) + " " + currLine);
			}
		}

		return verses;
	}

	protected void searchInBook(String bookID, String regQuery) {
		
		Book book = Books.get(bookID);

		BufferedReader bReader = this.getReader(book.getPath());
		if (bReader == null) {
			return;
		}

		String str;
		int chapter = this.containsChapterZero() ? -1 : 0;
		int verse = 0;
		try {
			while ((str = bReader.readLine()) != null) {
				str = str.replaceAll("\\s(\\d)+", "");
				if (str.toLowerCase().contains(ChapterSign)) {
					chapter++;
					verse = 0;
				}
				if (str.toLowerCase().contains(VerseSign))
					verse++;

				if (str.toLowerCase().matches(regQuery)) {
					String linkOSIS = this.ShortName + "." + bookID + "." + chapter + "." + verse;
					String content = StringProc.stripTags(str, this.getHtmlFilter(), true)
						.replaceAll("^\\d+\\s+", "");
					SearchRes.put(linkOSIS, content);
				}
			}
			bReader.close();
		} catch (IOException e) {
			Log.e(TAG, e);
		}
	}

}
