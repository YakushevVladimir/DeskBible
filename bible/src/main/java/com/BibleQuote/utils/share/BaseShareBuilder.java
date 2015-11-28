package com.BibleQuote.utils.share;

import android.content.Context;
import com.BibleQuote.modules.Book;
import com.BibleQuote.modules.Chapter;
import com.BibleQuote.modules.Module;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.bibleReferenceFormatter.FullReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.IBibleReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.ShortReferenceFormatter;
import com.BibleQuote.utils.bibleReferenceFormatter.EmptyReferenceFormatter;
import com.BibleQuote.utils.textFormatters.BreakVerseBibleShareFormatter;
import com.BibleQuote.utils.textFormatters.IShareTextFormatter;
import com.BibleQuote.utils.textFormatters.SimpleBibleShareFormatter;

import java.util.LinkedHashMap;
import java.util.TreeSet;

public abstract class BaseShareBuilder {
	IShareTextFormatter textFormater;
	IBibleReferenceFormatter referenceFormatter;

	Context context;
	Module module;
	Book book;
	Chapter chapter;
	LinkedHashMap<Integer, String> verses;

	protected void initFormatters() {
		if (PreferenceHelper.divideTheVerses()) {
			textFormater = new BreakVerseBibleShareFormatter(verses);
		} else {
			textFormater = new SimpleBibleShareFormatter(verses);
		}

		TreeSet<Integer> verseNumbers = new TreeSet<Integer>();
		for (Integer numb : verses.keySet()) {
			verseNumbers.add(numb);
		}

		String chapterNumber = String.valueOf(chapter.getNumber());
		if (!PreferenceHelper.addReference()) {
			referenceFormatter = new EmptyReferenceFormatter(module, book, chapterNumber, verseNumbers);
		} else if (PreferenceHelper.shortReference()) {
			referenceFormatter = new ShortReferenceFormatter(module, book, chapterNumber, verseNumbers);
		} else {
			referenceFormatter = new FullReferenceFormatter(module, book, chapterNumber, verseNumbers);
		}
	}

	protected String getShareText() {
		String text = textFormater.format();
		if (!PreferenceHelper.addReference()) {
			return text;
		}

		String reference = referenceFormatter.getLink();
		if (PreferenceHelper.putReferenceInBeginning()) {
			return String.format("%1$s - %2$s", reference, text);
		} else {
			return String.format("%1$s (%2$s)", text, reference);
		}
	}

	public abstract void share();
}
