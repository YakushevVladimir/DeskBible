package com.BibleQuote.managers;

//import java.util.TreeMap;  // поддержка с Android API 9 (Android 2.3.1)
import edu.emory.mathcs.backport.java.util.TreeMap;  // поддержка с Android API 5 (Android 2.0)

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 04.07.13
 * Time: 8:53
 * To change this template use File | Settings | File Templates.
 */

public class VersificationMap {

	private String sEtalonName = "Bible.KJV";

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


	public VersificationMap(Document docVersMap) {

		if (docVersMap != null) {
			try {

				XPath xpSelector = XPathFactory.newInstance().newXPath();

				boolean isToEtalon = true;
				do {

					MapBooks mapBooks = new MapBooks();

					String sXPExpr = null;
					if (isToEtalon) {
						sXPExpr = "/refSys/refMap[@to='" + sEtalonName + "']/map";
					} else {
						sXPExpr = "/refSys/refMap[@from='" + sEtalonName + "']/map";
					}

					NodeList ndlMapBooks = (NodeList) xpSelector.evaluate(sXPExpr, docVersMap, XPathConstants.NODESET);


					for (int iMpBk = 0; iMpBk < ndlMapBooks.getLength(); iMpBk++) {
						Node ndMapBook = ndlMapBooks.item(iMpBk);


						if (ndMapBook != null) {

							sXPExpr = "@forBook";
							String sForBook = xpSelector.evaluate(sXPExpr, ndMapBook);

							if (sForBook.length() != 0) {

								MapChapters mapChapters = new MapChapters();


								sXPExpr = "map";
								NodeList ndlMapChapters = (NodeList) xpSelector.evaluate(sXPExpr, ndMapBook, XPathConstants.NODESET);


								for (int iMpCh = 0; iMpCh < ndlMapChapters.getLength(); iMpCh++) {
									Node ndMapChapter = ndlMapChapters.item(iMpCh);

									if (ndMapChapter != null) {

										sXPExpr = "@startChapter";
										String sStartChapter = xpSelector.evaluate(sXPExpr, ndMapChapter);

										sXPExpr = "@endChapter";
										String sEndChapter = xpSelector.evaluate(sXPExpr, ndMapChapter);


										int iStartChapter = 0;
										try {
											iStartChapter = Integer.parseInt(sStartChapter);
										} catch (NumberFormatException e) {
											iStartChapter = 0;
										}

										int iEndChapter = 0;
										try {
											iEndChapter = Integer.parseInt(sEndChapter);
										} catch (NumberFormatException e) {
											iEndChapter = 0;
										}


										if (iStartChapter != 0 && iEndChapter != 0) {

											MapVerses mapVerses = new MapVerses();


											sXPExpr = "map";
											NodeList ndlMapVerses = (NodeList) xpSelector.evaluate(sXPExpr, ndMapChapter, XPathConstants.NODESET);


											for (int iMpVs = 0; iMpVs < ndlMapVerses.getLength(); iMpVs++) {
												Node ndMapVerse = ndlMapVerses.item(iMpVs);

												if (ndMapVerse != null) {

													sXPExpr = "@startVerse";
													String sStartVerse = xpSelector.evaluate(sXPExpr, ndMapVerse);

													sXPExpr = "@endVerse";
													String sEndVerse = xpSelector.evaluate(sXPExpr, ndMapVerse);


													int iStartVerse = 0;
													try {
														iStartVerse = Integer.parseInt(sStartVerse);
													} catch (NumberFormatException e) {
														iStartVerse = 0;
													}

													int iEndVerse = 0;
													try {
														iEndVerse = Integer.parseInt(sEndVerse);
													} catch (NumberFormatException e) {
														iEndVerse = 0;
													}


													if (iStartVerse != 0 && iEndVerse != 0) {

														sXPExpr = "@difCh";
														String sDifCh = xpSelector.evaluate(sXPExpr, ndMapVerse);

														sXPExpr = "@difVs";
														String sDifVs = xpSelector.evaluate(sXPExpr, ndMapVerse);


														int iDifCh = 0;
														try {
															iDifCh = Integer.parseInt(sDifCh);
														} catch (NumberFormatException e) {
															iDifCh = 0;
														}

														int iDifVs = 0;
														try {
															iDifVs = Integer.parseInt(sDifVs);
														} catch (NumberFormatException e) {
															iDifVs = 0;
														}


														int iVsRepeated = 0;
														int iCountSequence = 0;


														// атрибуты "@countSequence" и "@vsRepeated" должны быть только для одного стиха
														if (iStartVerse == iEndVerse) {

															sXPExpr = "@vsRepeated";
															String sVsRepeated = xpSelector.evaluate(sXPExpr, ndMapVerse);

															sXPExpr = "@countSequence";
															String sCountSequence = xpSelector.evaluate(sXPExpr, ndMapVerse);


															try {
																iVsRepeated = Integer.parseInt(sVsRepeated);
															} catch (NumberFormatException e) {
																iVsRepeated = 0;
															}

															try {
																iCountSequence = Integer.parseInt(sCountSequence);
															} catch (NumberFormatException e) {
																iCountSequence = 0;
															}
														}


														VerseDifferences verseDifferences =
																new VerseDifferences(iDifCh, iDifVs, iVsRepeated, iCountSequence);

														mapVerses.putValueToRangeOfKeys(iStartVerse, iEndVerse, verseDifferences);
													}
												}
											}

											mapChapters.putValueToRangeOfKeys(iStartChapter, iEndChapter, mapVerses);
										}
									}
								}

								mapBooks.put(sForBook, mapChapters);
							}
						}
					}


					if (isToEtalon) {
						toEtalonMap = mapBooks;
					} else {
						fromEtalonMap = mapBooks;
					}

					isToEtalon = !isToEtalon;
				} while (!isToEtalon);

			} catch (XPathExpressionException e) {

				// TODO заменить e.printStackTrace()

				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

		// TODO speedup

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
