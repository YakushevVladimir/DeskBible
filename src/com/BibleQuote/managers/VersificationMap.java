package com.BibleQuote.managers;

//import java.util.TreeMap;  // поддержка с Android API 9 (Android 2.3.1)
import edu.emory.mathcs.backport.java.util.TreeMap;  // поддержка с Android API 5 (Android 2.0)

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 04.07.13
 * Time: 8:53
 * To change this template use File | Settings | File Templates.
 */

public class VersificationMap {

	private final static String sEtalonName = "Bible.KJV";

	private MapBooks toEtalonMap;
	private MapBooks fromEtalonMap;


	public class VerseDifferences {
		public int iDifCh;
		public int iDifVs;
		public int iVsRepeated;
		public int iCountSequence;

		public VerseDifferences(int iDifCh, int iDifVs, int iVsRepeated, int iCountSequence) {
			this.iDifCh = iDifCh;
			this.iDifVs = iDifVs;
			this.iVsRepeated = iVsRepeated;
			this.iCountSequence = iCountSequence;
		}

		public VerseDifferences() {
			this.iDifCh = 0;
			this.iDifVs = 0;
			this.iVsRepeated = 0;
			this.iCountSequence = 0;
		}
	}


	/*private class MapBooks extends TreeMap<String, MapChapters> {
	}*/

	private class MapBooks extends TreeMap {
	}

	private class MapChapters extends RangeTreeMap {
	}

	private class MapVerses extends RangeTreeMap {
	}


	/*private class RangeTreeMap<V> extends TreeMap<Integer, V> {

		public V getValueFromRangeOfKeys(Integer key) {
			Entry<Integer, V> e = floorEntry(key);

			// Если необходимо включить и верхнюю границу диапазона -- раскомментировать
			//if (e != null && e.getValue() == null) {
			//	e = lowerEntry(key);
			//}

			return e == null ? null : e.getValue();
		}

		public void putValueToRangeOfKeys(Integer keyFrom, Integer keyTo, V value) {
			put(keyFrom, value);
			put(++keyTo, null);
		}
	}*/


	private class RangeTreeMap extends TreeMap {

		public Object getValueFromRangeOfKeys(Integer key) {
			Map.Entry e = floorEntry(key);

			// Если необходимо включить и верхнюю границу диапазона -- раскомментировать
			//if (e != null && e.getValue() == null) {
			//	e = lowerEntry(key);
			//}

			return e == null ? null : e.getValue();
		}

		public void putValueToRangeOfKeys(Integer keyFrom, Integer keyTo, Object value) {
			put(keyFrom, value);
			put(++keyTo, null);
		}
	}


	public VersificationMap(Reader rdVersificationFile) {

		// получаем фабрику
		//XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		// включаем поддержку namespace (по умолчанию выключена)
		//factory.setNamespaceAware(true);


		// создаем парсер
		XmlPullParser xppVersMap = null;

		if (rdVersificationFile != null) {

			try {
				xppVersMap = XmlPullParserFactory.newInstance().newPullParser();

				// даем парсеру на вход Reader
				xppVersMap.setInput(rdVersificationFile);

			} catch (XmlPullParserException e) {

				xppVersMap = null;

				// TODO заменить e.printStackTrace()
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}


		if (xppVersMap != null) {

			try {

				MapBooks mapBooks = null;
				MapChapters mapChapters = null;
				MapVerses mapVerses = null;
				VerseDifferences verseDifferences = null;

				boolean isToEtalon = true;

				String sForBook = "";

				int iStartChapter = 0;
				int iEndChapter = 0;

				int iStartVerse = 0;
				int iEndVerse = 0;
				int iDifCh = 0;
				int iDifVs = 0;
				int iVsRepeated = 0;
				int iCountSequence = 0;

				int iDepth = 0;


				while (xppVersMap.getEventType() != XmlPullParser.END_DOCUMENT) {
					switch (xppVersMap.getEventType()) {

						// начало документа
						//case XmlPullParser.START_DOCUMENT:
						//	Log.d(LOG_TAG, "START_DOCUMENT");
						//	break;

						// начало тэга
						case XmlPullParser.START_TAG:
							switch (xppVersMap.getDepth()) {
								case 1:
									if (xppVersMap.getName().equals("refSys")) {
										iDepth++;
									}
									break;
								case 2:
									if (xppVersMap.getName().equals("refMap")) {
										iDepth++;

										mapBooks = null;

										for (int i = 0; i < xppVersMap.getAttributeCount(); i++) {

											if (xppVersMap.getAttributeValue(i).equals(sEtalonName)) {
												if (xppVersMap.getAttributeName(i).equals("to")) {
													isToEtalon = true;
													mapBooks = new MapBooks();
												}

												if (xppVersMap.getAttributeName(i).equals("from")) {
													isToEtalon = false;
													mapBooks = new MapBooks();
												}
											}
										}
									}
									break;
								case 3:
									if (xppVersMap.getName().equals("mapBk")) {
										iDepth++;

										mapChapters = null;
										sForBook = "";

										for (int i = 0; i < xppVersMap.getAttributeCount(); i++) {

											if (xppVersMap.getAttributeName(i).equals("forBook")) {

												sForBook = xppVersMap.getAttributeValue(i);
											}
										}


										if (sForBook.length() != 0) {

											mapChapters = new MapChapters();
										}
									}
									break;
								case 4:
									if (xppVersMap.getName().equals("mapCh")) {
										iDepth++;

										mapVerses = null;
										iStartChapter = 0;
										iEndChapter = 0;


										for (int i = 0; i < xppVersMap.getAttributeCount(); i++) {

											if (xppVersMap.getAttributeName(i).equals("startChapter")) {

												try {
													iStartChapter = Integer.parseInt(xppVersMap.getAttributeValue(i));
												} catch (NumberFormatException e) {
													iStartChapter = 0;
												}
											}

											if (xppVersMap.getAttributeName(i).equals("endChapter")) {

												try {
													iEndChapter = Integer.parseInt(xppVersMap.getAttributeValue(i));
												} catch (NumberFormatException e) {
													iEndChapter = 0;
												}
											}

										}


										if (iStartChapter > 0 && iEndChapter > 0) {

											mapVerses = new MapVerses();
										}
									}
									break;
								case 5:
									if (xppVersMap.getName().equals("mapVs")) {
										iDepth++;

										verseDifferences = null;

										iStartVerse = 0;
										iEndVerse = 0;
										iDifCh = 0;
										iDifVs = 0;
										iVsRepeated = 0;
										iCountSequence = 0;


										for (int i = 0; i < xppVersMap.getAttributeCount(); i++) {

											if (xppVersMap.getAttributeName(i).equals("startVerse")) {

												try {
													iStartVerse = Integer.parseInt(xppVersMap.getAttributeValue(i));
												} catch (NumberFormatException e) {
													iStartVerse = 0;
												}
											}

											if (xppVersMap.getAttributeName(i).equals("endVerse")) {

												try {
													iEndVerse = Integer.parseInt(xppVersMap.getAttributeValue(i));
												} catch (NumberFormatException e) {
													iEndVerse = 0;
												}
											}

											if (xppVersMap.getAttributeName(i).equals("difCh")) {

												try {
													iDifCh = Integer.parseInt(xppVersMap.getAttributeValue(i));
												} catch (NumberFormatException e) {
													iDifCh = 0;
												}
											}

											if (xppVersMap.getAttributeName(i).equals("difVs")) {

												try {
													iDifVs = Integer.parseInt(xppVersMap.getAttributeValue(i));
												} catch (NumberFormatException e) {
													iDifVs = 0;
												}
											}

											if (xppVersMap.getAttributeName(i).equals("vsRepeated")) {

												try {
													iVsRepeated = Integer.parseInt(xppVersMap.getAttributeValue(i));
												} catch (NumberFormatException e) {
													iVsRepeated = 0;
												}
											}

											if (xppVersMap.getAttributeName(i).equals("countSequence")) {

												try {
													iCountSequence = Integer.parseInt(xppVersMap.getAttributeValue(i));
												} catch (NumberFormatException e) {
													iCountSequence = 0;
												}
											}
										}


										if (iStartVerse > 0 && iEndVerse > 0) {

											// атрибуты "@countSequence" и "@vsRepeated" должны быть только для одного стиха
											if (iStartVerse != iEndVerse) {
												iVsRepeated = 0;
												iCountSequence = 0;
											}

											if (iVsRepeated < 0) {
												iVsRepeated = 0;
											}

											if (iCountSequence < 0) {
												iCountSequence = 0;
											}

											if (iDifCh != 0 || iDifVs != 0 || iVsRepeated > 0 || iCountSequence > 0) {

												verseDifferences =
														new VerseDifferences(iDifCh, iDifVs, iVsRepeated, iCountSequence);
											}
										}
									}
									break;
								default:
									break;
							}

							break;

						// конец тэга
						case XmlPullParser.END_TAG:
							switch (xppVersMap.getDepth()) {
								case 1:
									if (xppVersMap.getName().equals("refSys")) {

										iDepth--;
									}
									break;
								case 2:
									if (xppVersMap.getName().equals("refMap")) {

										if (iDepth == 2) {
											if (isToEtalon) {
												toEtalonMap = mapBooks;
											} else {
												fromEtalonMap = mapBooks;
											}
										}

										iDepth--;
									}
									break;
								case 3:
									if (xppVersMap.getName().equals("mapBk")) {

										if (iDepth == 3 && mapBooks != null && mapChapters != null) {

											mapBooks.put(sForBook, mapChapters);
										}

										iDepth--;
									}
								case 4:
									if (xppVersMap.getName().equals("mapCh")) {

										if (iDepth == 4 && mapChapters != null && mapVerses != null) {

											mapChapters.putValueToRangeOfKeys(iStartChapter, iEndChapter, mapVerses);
										}

										iDepth--;
									}
									break;
								case 5:
									if (xppVersMap.getName().equals("mapVs")) {

										if (iDepth == 5 && mapVerses != null && verseDifferences != null) {

											mapVerses.putValueToRangeOfKeys(iStartVerse, iEndVerse, verseDifferences);
										}

										iDepth--;
									}
									break;
								default:
									break;
							}
							break;

						// содержимое тэга
						//case XmlPullParser.TEXT:
						//	Log.d(LOG_TAG, "text = " + xppVersMap.getText());
						//	break;

						default:
							break;
					}

					// следующий элемент
					xppVersMap.next();
				}


				//Llog.d(LOG_TAG, "END_DOCUMENT");


				if (iDepth != 0) {
					toEtalonMap = null;
					fromEtalonMap = null;
				}


			} catch (XmlPullParserException e) {

				// TODO заменить e.printStackTrace();
				e.printStackTrace();

				toEtalonMap = null;
				fromEtalonMap = null;

			} catch (IOException e) {

				// TODO заменить e.printStackTrace();
				e.printStackTrace();

				toEtalonMap = null;
				fromEtalonMap = null;

			}


		} else {
			toEtalonMap = null;
			fromEtalonMap = null;
		}
	}


	/*public VerseDifferences getMapForVerse(boolean isEtalon, String sForBook, Integer iChapter, Integer iVerse) {

		// TODO speedup

		VerseDifferences verseDifferences = null;

		MapBooks mapBooks = isEtalon ? toEtalonMap : fromEtalonMap;

		if (mapBooks != null) {
			MapChapters mapChapters = mapBooks.get(sForBook);

			if (mapChapters != null) {
				MapVerses mapVerses = mapChapters.getValueFromRangeOfKeys(iChapter);

				if (mapVerses != null) {
					verseDifferences = mapVerses.getValueFromRangeOfKeys(iVerse);
				}
			}
		}


		return verseDifferences != null ? verseDifferences : new VerseDifferences();
	}*/


	public VerseDifferences getMapForVerse(boolean isEtalon, String sForBook, Integer iChapter, Integer iVerse) {

		VerseDifferences verseDifferences = null;

		MapBooks mapBooks = isEtalon ? toEtalonMap : fromEtalonMap;

		if (mapBooks != null) {
			MapChapters mapChapters = (MapChapters) mapBooks.get(sForBook);

			if (mapChapters != null) {
				MapVerses mapVerses = (MapVerses) mapChapters.getValueFromRangeOfKeys(iChapter);

				if (mapVerses != null) {
					verseDifferences = (VerseDifferences) mapVerses.getValueFromRangeOfKeys(iVerse);
				}
			}
		}


		return verseDifferences != null ? verseDifferences : new VerseDifferences();
	}

}
