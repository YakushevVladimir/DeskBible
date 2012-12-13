package com.BibleQuote.dal.repository;

import com.BibleQuote.exceptions.BQUniversalException;
import com.BibleQuote.exceptions.TskNotFoundException;

public interface ITskRepository {
	String getReferences(String book, String chapter, String verse) throws TskNotFoundException, BQUniversalException;
}
