package com.BibleQuote.utils.BibleReferenceFormatter;

import java.util.TreeSet;

import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;

public class EmptyReferenceFormatter extends ReferenceFormatter implements IBibleReferenceFormatter {

	public EmptyReferenceFormatter(Module module, Book book, String chapter,
			TreeSet<Integer> verses) {
		super(module, book, chapter, verses);
	}

	@Override
	public String getLink() {
		return "";
	}

}
