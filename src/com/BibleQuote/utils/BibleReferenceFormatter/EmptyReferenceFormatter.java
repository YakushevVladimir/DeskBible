package com.BibleQuote.utils.BibleReferenceFormatter;

import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Module;

import java.util.TreeSet;

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
