package com.BibleQuote.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.BibleQuote.models.Chapter;

public class ChapterPool extends LinkedHashMap<String, Chapter> {
	private static final long serialVersionUID = -366362500655090729L;
	private static final int MAX_POOL_SIZE = 20;

	public ChapterPool() {
		super(MAX_POOL_SIZE, .75f, true);
	}
	
    protected boolean removeEldestEntry(@SuppressWarnings("rawtypes") Map.Entry eldest) {
        return size() > MAX_POOL_SIZE;
    }
}
