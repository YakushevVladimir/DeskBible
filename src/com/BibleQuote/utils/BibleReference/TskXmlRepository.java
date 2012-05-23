package com.BibleQuote.utils.BibleReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.BibleQuote.utils.DataConstants;

import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

public class TskXmlRepository implements ITskRepository {
	
	final static String TAG = "TskXmlRepository";
	
	final static String DOCUMENT = "tsk";
	final static String BOOK = "book";
	final static String CHAPTER = "chapter";
	final static String VERSE = "verse";
	
	@Override
	public String getReferences(String book, String chapter, String verse) {
		
		String references = "";
		
		XmlPullParser parser;
		try {
			parser = getParser();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return references;
		}
		
        try {
			int eventType = parser.getEventType();
	        boolean done = false;
	        boolean bookFind = false;
	        boolean chapterFind = false;
	        while (eventType != XmlResourceParser.END_DOCUMENT && !done){
	            String name = null;
	            switch (eventType){
	                case XmlResourceParser.START_TAG:
	                    name = parser.getName();
	                    if (name.equalsIgnoreCase(BOOK)){
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
	                    if (name.equalsIgnoreCase(DOCUMENT)){
	                    	done = true;
	                    }
	                    break;
	            }
	            eventType = parser.next();
	        }
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} catch (XmlPullParserException e) {
			Log.e(TAG, e.toString());
		}
		
		return references;
	}

	private XmlPullParser getParser() throws XmlPullParserException, FileNotFoundException, UnsupportedEncodingException {
		
		File tskDir = new File(DataConstants.FS_EXTERNAL_OTHER_PATH);
		File tsk = new File(tskDir, "tsk.xml");
		
		InputStreamReader iReader = new InputStreamReader(new FileInputStream(tsk), "UTF8");
		BufferedReader buf = new BufferedReader(iReader);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(buf);
		return parser;
	}
}
