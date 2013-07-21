package com.BibleQuote.modules;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 29.06.13
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */

public class EtalonChapter {

	public String Book_OSIS_ID;

	private ArrayList<EtalonVerse> etVerses;

	public EtalonChapter() {
		etVerses = new ArrayList<EtalonVerse>(200);
	}

	public EtalonVerse get(int iSeq) {
		return etVerses.get(iSeq);
	}

	public boolean put(int iCh, int iVs) {

		int iSize = etVerses.size();
		EtalonVerse etVerse;

		if (iSize != 0) {
			etVerse = etVerses.get(iSize - 1);
			if (etVerse != null  &&  etVerse.iChapterNumber == iCh  &&  etVerse.iVerseNumber == iVs) {
				return false;
			}
		}

		etVerse = new EtalonVerse(iCh, iVs);
		return etVerses.add(etVerse);
	}

	public int size() {
		return etVerses.size();
	}
}
