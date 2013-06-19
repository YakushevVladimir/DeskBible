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
package com.BibleQuote.managers;

import android.content.Context;
import com.BibleQuote.controllers.*;
import com.BibleQuote.dal.repository.XmlTskRepository;
import com.BibleQuote.dal.repository.fsHistoryRepository;
import com.BibleQuote.entity.BibleBooksID;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.entity.ItemList;
import com.BibleQuote.exceptions.*;
import com.BibleQuote.managers.History.IHistoryManager;
import com.BibleQuote.managers.History.SimpleHistoryManager;
import com.BibleQuote.modules.*;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.PreferenceHelper;
import com.BibleQuote.utils.Share.ShareBuilder;
import com.BibleQuote.utils.Share.ShareBuilder.Destination;
import com.BibleQuote.utils.StringProc;
import com.BibleQuote.utils.XmlUtil;
import com.BibleQuote.utils.modules.LinkConverter;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Librarian {

	private final String TAG = "Librarian";

	private LinkedHashMap<String, String> searchResults = new LinkedHashMap<String, String>();

	private Module currModule;
	private Book currBook;
	private Chapter currChapter;
	private Integer currChapterNumber = -1;
	private Integer currVerseNumber = 1;

	public boolean isShowParTranslates = false;

	public String ParModuleID = "";
	public Chapter ParChapter;

	private IHistoryManager historyManager;

	private IModuleController moduleCtrl;
	private IBookController bookCtrl;
	private IChapterController chapterCtrl;
	private TSKController tskCtrl;

	private LibraryController libCtrl;

	/**
	 * Инициализация контроллеров библиотеки, модулей, книг и глав.
	 * Подписка на событие ChangeBooksEvent
	 */
	public Librarian(Context context) {
		Log.i(TAG, "Create library controllers");
		libCtrl = LibraryController.create(context);
		moduleCtrl = libCtrl.getModuleCtrl();
		bookCtrl = libCtrl.getBookCtrl();
		chapterCtrl = libCtrl.getChapterCtrl();

		Log.i(TAG, "Create history manager and repository");
		fsHistoryRepository repository = new fsHistoryRepository(context.getCacheDir());
		historyManager = new SimpleHistoryManager(repository, PreferenceHelper.getHistorySize());

		ParModuleID = PreferenceHelper.restoreStateString("ParModuleID");
		isShowParTranslates = (ParModuleID.length() != 0) && PreferenceHelper.restoreStateBoolean("isShowParTranslates");
		PreferenceHelper.saveStateBoolean("isShowParTranslates", isShowParTranslates);

		getModules();
	}

	public EventManager getEventManager() {
		return libCtrl.getEventManager();
	}

	/**
	 * Загружает из хранилища список модулей без загрузки их данных. Для каждого из модулей
	 * установлен флаг isClosed = true.
	 * @return Возвращает TreeMap, где в качестве ключа путь к модулю, а в качестве значения 
	 * closed-модуль
	 */
	/**
	 * Возвращает коллекцию Book для указанного модуля. Данные о книгах в первую
	 * очередь берутся из контекста библиотеки. Если там для выбранного модуля
	 * список книг отсутсвует, то производится загрузка коллекции Book из хранилища
	 *
	 * @param module модуль для которого необходимо получить коллекцию Book
	 * @return коллекцию Book для указанного модуля
	 * @throws OpenModuleException
	 * @throws BooksDefinitionException
	 * @throws BookDefinitionException
	 */
	public ArrayList<Book> getBookList(Module module) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		return bookCtrl.getBookList(module);
	}

	/**
	 * Инициализирует полную загрузку модулей. Сначала проверяется наличие
	 * модулей в коллекции. Если коллекция пуста, то производится попытка
	 * загрузки коллекции модулей из кэш. Иначе производится загрузка модулей
	 * из файлового хранилища. Производится запись загруженных модулей в кэш.
	 */
	public void loadFileModules() {
		moduleCtrl.loadFileModules();
	}

	public void getModules() {
		moduleCtrl.getModules();
	}

	public Module getModuleByID(String moduleID) throws OpenModuleException {
		return moduleCtrl.getModuleByID(moduleID);
	}

	public Book getBookByID(Module module, String bookID) throws BookNotFoundException, OpenModuleException {
		return bookCtrl.getBookByID(module, bookID);
	}

	public Chapter getChapterByNumber(Book book, Integer chapterNumber) throws BookNotFoundException {
		return chapterCtrl.getChapter(book, chapterNumber);
	}

	public Chapter openChapter(BibleReference link) throws BookNotFoundException, OpenModuleException {
		currModule = getModuleByID(link.getModuleID());
		currBook = getBookByID(getCurrModule(), link.getBookID());
		currChapter = getChapterByNumber(getCurrBook(), link.getChapter());
		currChapterNumber = link.getChapter();
		currVerseNumber = link.getFromVerse();

		historyManager.addLink(new BibleReference(getCurrModule(), getCurrBook(), getCurrChapterNumber(), getCurrVerseNumber()));

		if (isShowParTranslates) {
			openParChapter(ParModuleID);
		}

		return getCurrChapter();
	}


	/*
	// Функция с учетом атрибута IntoBook, отсавлена для архива
	public Chapter ChapterVersification_IntoBook(Chapter fromChapter, Module toModule, Document docVersificationMap,
										FileOutputStream fosLogErr)
			throws BookNotFoundException, OpenModuleException {


		int iChapNumber1 = fromChapter.getNumber();
		String sChapNumber1 = Integer.toString(iChapNumber1);
		int iChapSize1 = fromChapter.getChapterSize();

		String sBookOsisID1 = fromChapter.getBook().OSIS_ID;
		String sChapterOsisID1 = sBookOsisID1 + "." + sChapNumber1;


		Book Book2 = null;
		if (toModule != null) {

			// TODO new context
			Book2 = getBookByID(toModule, sBookOsisID1);
		}

		int iChapNumber2 = iChapNumber1;

		Chapter Chapter2 = null;
		if (Book2 != null) {

			// TODO new context
			Chapter2 = getChapterByNumber(Book2, iChapNumber2);
		}

		// if (Book2 == null) -- то не важно
		Chapter returnChapter = new Chapter(Book2, iChapNumber2);

		boolean isVersificationMap = (docVersificationMap != null);
		boolean isLogErr = (fosLogErr != null);

		XPath xpSelector = null;
		String sXPExpr = null;
		Node ndMapBook = null;
		NodeList ndlMapChapters = null;
		int iMapChapSize = 0;

		try {
			if (isVersificationMap) {
				xpSelector = XPathFactory.newInstance().newXPath();

				sXPExpr = "/refSys/refMap/map[@forBook='" + sBookOsisID1 + "']";
				ndMapBook = (Node) xpSelector.evaluate(sXPExpr, docVersificationMap, XPathConstants.NODE);

				sXPExpr = "map";
				ndlMapChapters = (NodeList) xpSelector.evaluate(sXPExpr, ndMapBook, XPathConstants.NODESET);
				iMapChapSize = ndlMapChapters.getLength();
			}

			boolean isMapForBook = isVersificationMap && (iMapChapSize != 0);
			boolean isMapForChapter = false;

			NodeList ndlMapVerses = null;
			int iMapVsSize = 0;

			Node ndMapChapter = null;
			Node ndMapVerse = null;

			String sStartChapter = "";
			String sEndChapter = "";
			int iStartChapter = 0;
			int iEndChapter = 0;

			if (isMapForBook) {

				int iMpCh = 0;

				while ((iMpCh < iMapChapSize) && (!isMapForChapter)) {

					ndMapChapter = ndlMapChapters.item(iMpCh);

					sXPExpr = "@startChapter";
					sStartChapter = xpSelector.evaluate(sXPExpr, ndMapChapter);
					iStartChapter = Integer.parseInt(sStartChapter);

					sXPExpr = "@endChapter";
					sEndChapter = xpSelector.evaluate(sXPExpr, ndMapChapter);
					iEndChapter = Integer.parseInt(sEndChapter);

					if ((iStartChapter <= iChapNumber1) && (iChapNumber1 <= iEndChapter)) {
						isMapForChapter = true;
					}

					iMpCh++;

				}

				sXPExpr = "map";
				ndlMapVerses = (NodeList) xpSelector.evaluate(sXPExpr, ndMapChapter, XPathConstants.NODESET);
				iMapVsSize = ndlMapVerses.getLength();
			}


			String sChapNumber2 = sChapNumber1;

			int iStartVerse = 0;
			int iEndVerse = 0;

			int iDifVs = 0;

			boolean isChapterWithErr = false;

			for (int iVsNumber1 = 1; iVsNumber1 <= iChapSize1; iVsNumber1++) {

				//String sVsNumber1 = Integer.toString(iVsNumber1);
				//String sVsNumber2 = sVsNumber1;
				//String sVerseOsisID1 = sChapterOsisID1 + "." + sVsNumber1;
				//String sBookOsisID2 = sBookOsisID1;
				//String sChapterOsisID2 = sChapterOsisID1;
				//String sVerseOsisID2 = sVerseOsisID1;

				boolean isVerse2 = true;
				boolean isMapForVerse = false;

				if (isMapForChapter) {
					if ( (iEndVerse == 0) || (iEndVerse < iVsNumber1) ) {

						// speedup (вынос выделения памяти под переменные прироста скорости не дал)

						iStartVerse = 0;
						iEndVerse = 0;

						String sStartVerse = "";
						String sEndVerse = "";

						int iMpVs = 0;

						while (iMpVs < iMapVsSize) {

							ndMapVerse = ndlMapVerses.item(iMpVs);

							// speedup (оптимизация запроса XPath дала выигрыш 4 секунды на Псалме 119(118))

							sXPExpr = "@startVerse";
							sStartVerse = xpSelector.evaluate(sXPExpr, ndMapVerse);
							iStartVerse = Integer.parseInt(sStartVerse);

							sXPExpr = "@endVerse";
							sEndVerse = xpSelector.evaluate(sXPExpr, ndMapVerse);
							iEndVerse = Integer.parseInt(sEndVerse);

							if (iVsNumber1 < iStartVerse) {
								break;
							}

							if ((iStartVerse <= iVsNumber1) && (iVsNumber1 <= iEndVerse)) {
								isMapForVerse = true;
								break;
							}

							iMpVs++;
						}

						if (iEndVerse < iVsNumber1) {

							Book2 = getBookByID(toModule, sBookOsisID1);

							iChapNumber2 = iChapNumber1;
							sChapNumber2 = sChapNumber1;

							if (Book2 != null) {

								// TODO new context
								Chapter2 = getChapterByNumber(Book2, iChapNumber2);
							}

							iDifVs = 0;
							iEndVerse = 999; // to end of chapter
						}
					}


					if (isMapForVerse || (iVsNumber1 == iStartVerse)) {

						isMapForVerse = false;

						sXPExpr = "@intoBook";
						String sIntoBook = xpSelector.evaluate(sXPExpr, ndMapVerse);

						sXPExpr = "@difCh";
						String sDifCh = xpSelector.evaluate(sXPExpr, ndMapVerse);

						sXPExpr = "@difVs";
						String sDifVs = xpSelector.evaluate(sXPExpr, ndMapVerse);

						if (sIntoBook.compareTo("-") == 0
								&& sDifCh.compareTo("-") == 0
								&& sDifVs.compareTo("-") == 0) {
							isVerse2 = false;
						} else {

							// speedup (редко выполняется, чтобы это делать)

							// TODO new context
							Book2 = getBookByID(toModule, sIntoBook);

							int iDifCh = Integer.parseInt(sDifCh);
							iChapNumber2 = iChapNumber1 + iDifCh;
							sChapNumber2 = Integer.toString(iChapNumber2);

							if (Book2 != null) {

								// TODO new context
								Chapter2 = getChapterByNumber(Book2, iChapNumber2);
							}

							iDifVs = Integer.parseInt(sDifVs);

							//sBookOsisID2 = sIntoBook;
							//sChapterOsisID2 = sBookOsisID2 + "." + sChapNumber2;
						}
					}
				}


				int iVsNumber2 = iVsNumber1 + iDifVs;
				//sVsNumber2 = Integer.toString(iVsNumber2);
				//sVerseOsisID2 = sChapterOsisID2 + "." + sVsNumber2;

				String sVerseText2 = "---";

				if (isVerse2) {
					if (Chapter2 != null) {

						// speedup (введение Chapter2 прироста скорости не дал)
						Verse vsVerse2 = Chapter2.getVerse(iVsNumber2);

						if (vsVerse2 != null) {

							if (!isLogErr) {
								sVerseText2 = vsVerse2.getText();

								// speedup (вынос Pattern.compile("\\d") за цикл прироста не дал)
								Matcher mMatcher = Pattern.compile("\\d").matcher(sVerseText2);

								if (mMatcher.find()) {

									// speedup (StringBuilder вместо "+" прироста скорости не дал)
									sVerseText2 = sVerseText2.substring(0, mMatcher.start())
											+ sChapNumber2 + ":" + sVerseText2.substring(mMatcher.start());
								}
							}
						} else isVerse2 = false;
					} else isVerse2 = false;
				}

				if (isLogErr && !isVerse2) {
					String sVsNumber1 = Integer.toString(iVsNumber1);
					String sVerseOsisID1 = sChapterOsisID1 + "." + sVsNumber1;

					fosLogErr.write(sVerseOsisID1.getBytes("UTF-8"));
					fosLogErr.write(0x0A);

					isChapterWithErr = true;

				}

				if (!isLogErr) {
					// TODO speedup (введение ParChapter.putVerse прироста скорости не дало. Но пока на 2-х переводах!)
					returnChapter.putVerse(new Verse((iVsNumber2 - 1), sVerseText2), iVsNumber1);
				}

			}

			if (!isLogErr) {
				isShowParTranslates = (returnChapter.getVerseNumber().equals(fromChapter.getVerseNumber()));
				PreferenceHelper.saveStateBoolean("isShowParTranslates", isShowParTranslates);
			}

			if (isLogErr && isChapterWithErr) {
				fosLogErr.write(0x0A);
			}

			return returnChapter;

		} catch (XPathExpressionException e) {

			// TODO заменить e.printStackTrace()

			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

			isShowParTranslates = false;
			PreferenceHelper.saveStateBoolean("isShowParTranslates", isShowParTranslates);

			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
	}
	*/

	// Функция без учета атрибута IntoBook, пока нет примеров надобности этого атрибута
	public Chapter ChapterVersification(Chapter fromChapter, Book toBook,
										Document docVersificationMap, FileOutputStream fosLogErr)
			throws BookNotFoundException, OpenModuleException {


		int iChapNumber1 = fromChapter.getNumber();
		String sChapNumber1 = Integer.toString(iChapNumber1);
		int iChapSize1 = fromChapter.getChapterSize();

		String sBookOsisID1 = fromChapter.getBook().OSIS_ID;
		String sChapterOsisID1 = sBookOsisID1 + "." + sChapNumber1;


		Book Book2 = toBook;

		int iChapNumber2 = iChapNumber1;

		Chapter Chapter2 = null;
		if (Book2 != null) {
			Chapter2 = getChapterByNumber(Book2, iChapNumber2);
		}

		// if (Book2 == null) -- то не важно
		Chapter returnChapter = new Chapter(Book2, iChapNumber2);

		boolean isVersificationMap = (docVersificationMap != null);
		boolean isLogErr = (fosLogErr != null);

		XPath xpSelector = null;
		String sXPExpr = null;
		Node ndMapBook = null;
		NodeList ndlMapChapters = null;
		int iMapChapSize = 0;

		try {
			if (isVersificationMap) {
				xpSelector = XPathFactory.newInstance().newXPath();

				sXPExpr = "/refSys/refMap/map[@forBook='" + sBookOsisID1 + "']";
				ndMapBook = (Node) xpSelector.evaluate(sXPExpr, docVersificationMap, XPathConstants.NODE);

				sXPExpr = "map";
				ndlMapChapters = (NodeList) xpSelector.evaluate(sXPExpr, ndMapBook, XPathConstants.NODESET);
				iMapChapSize = ndlMapChapters.getLength();
			}

			boolean isMapForBook = isVersificationMap && (iMapChapSize != 0);
			boolean isMapForChapter = false;

			NodeList ndlMapVerses = null;
			int iMapVsSize = 0;

			Node ndMapChapter = null;
			Node ndMapVerse = null;

			String sStartChapter = "";
			String sEndChapter = "";
			int iStartChapter = 0;
			int iEndChapter = 0;

			if (isMapForBook) {

				int iMpCh = 0;

				while ((iMpCh < iMapChapSize) && (!isMapForChapter)) {

					ndMapChapter = ndlMapChapters.item(iMpCh);

					sXPExpr = "@startChapter";
					sStartChapter = xpSelector.evaluate(sXPExpr, ndMapChapter);
					iStartChapter = Integer.parseInt(sStartChapter);

					sXPExpr = "@endChapter";
					sEndChapter = xpSelector.evaluate(sXPExpr, ndMapChapter);
					iEndChapter = Integer.parseInt(sEndChapter);

					if ((iStartChapter <= iChapNumber1) && (iChapNumber1 <= iEndChapter)) {
						isMapForChapter = true;
					}

					iMpCh++;

				}

				sXPExpr = "map";
				ndlMapVerses = (NodeList) xpSelector.evaluate(sXPExpr, ndMapChapter, XPathConstants.NODESET);
				iMapVsSize = ndlMapVerses.getLength();
			}


			String sChapNumber2 = sChapNumber1;

			int iStartVerse = 0;
			int iEndVerse = 0;

			int iDifVs = 0;

			boolean isChapterWithErr = false;

			for (int iVsNumber1 = 1; iVsNumber1 <= iChapSize1; iVsNumber1++) {

				//String sVsNumber1 = Integer.toString(iVsNumber1);
				//String sVsNumber2 = sVsNumber1;
				//String sVerseOsisID1 = sChapterOsisID1 + "." + sVsNumber1;
				//String sBookOsisID2 = sBookOsisID1;
				//String sChapterOsisID2 = sChapterOsisID1;
				//String sVerseOsisID2 = sVerseOsisID1;

				boolean isVerse2 = true;
				boolean isMapForVerse = false;

				if (isMapForChapter) {
					if ( (iEndVerse == 0) || (iEndVerse < iVsNumber1) ) {

						// speedup (вынос выделения памяти под переменные прироста скорости не дал)

						iStartVerse = 0;
						iEndVerse = 0;

						String sStartVerse = "";
						String sEndVerse = "";

						int iMpVs = 0;

						while (iMpVs < iMapVsSize) {

							ndMapVerse = ndlMapVerses.item(iMpVs);

							// speedup (оптимизация запроса XPath дала выигрыш 4 секунды на Псалме 119(118))

							sXPExpr = "@startVerse";
							sStartVerse = xpSelector.evaluate(sXPExpr, ndMapVerse);
							iStartVerse = Integer.parseInt(sStartVerse);

							sXPExpr = "@endVerse";
							sEndVerse = xpSelector.evaluate(sXPExpr, ndMapVerse);
							iEndVerse = Integer.parseInt(sEndVerse);

							if (iVsNumber1 < iStartVerse) {
								break;
							}

							if ((iStartVerse <= iVsNumber1) && (iVsNumber1 <= iEndVerse)) {
								isMapForVerse = true;
								break;
							}

							iMpVs++;
						}

						if (iEndVerse < iVsNumber1) {

							iChapNumber2 = iChapNumber1;
							sChapNumber2 = sChapNumber1;

							if (Book2 != null) {
								Chapter2 = getChapterByNumber(Book2, iChapNumber2);
							}

							iDifVs = 0;
							iEndVerse = 999; // to end of chapter
						}
					}


					if (isMapForVerse || (iVsNumber1 == iStartVerse)) {

						isMapForVerse = false;

						sXPExpr = "@difCh";
						String sDifCh = xpSelector.evaluate(sXPExpr, ndMapVerse);

						sXPExpr = "@difVs";
						String sDifVs = xpSelector.evaluate(sXPExpr, ndMapVerse);


						if (sDifCh.compareTo("-") == 0  &&  sDifVs.compareTo("-") == 0) {
							isVerse2 = false;
						} else {

							// speedup (редко выполняется, чтобы это делать)

							int iDifCh = Integer.parseInt(sDifCh);
							iChapNumber2 = iChapNumber1 + iDifCh;
							sChapNumber2 = Integer.toString(iChapNumber2);

							if (Book2 != null) {
								Chapter2 = getChapterByNumber(Book2, iChapNumber2);
							}

							iDifVs = Integer.parseInt(sDifVs);

							//sBookOsisID2 = sIntoBook;
							//sChapterOsisID2 = sBookOsisID2 + "." + sChapNumber2;
						}
					}
				}


				int iVsNumber2 = iVsNumber1 + iDifVs;
				//sVsNumber2 = Integer.toString(iVsNumber2);
				//sVerseOsisID2 = sChapterOsisID2 + "." + sVsNumber2;

				String sVerseText2 = "---";

				if (isVerse2) {
					if (Chapter2 != null) {

						// speedup (введение Chapter2 прироста скорости не дал)
						Verse vsVerse2 = Chapter2.getVerse(iVsNumber2);

						if (vsVerse2 != null) {

							if (!isLogErr) {
								sVerseText2 = vsVerse2.getText();

								// speedup (вынос Pattern.compile("\\d") за цикл прироста не дал)
								Matcher mMatcher = Pattern.compile("\\d").matcher(sVerseText2);

								if (mMatcher.find()) {

									// speedup (StringBuilder вместо "+" прироста скорости не дал)
									sVerseText2 = sVerseText2.substring(0, mMatcher.start())
											+ sChapNumber2 + ":" + sVerseText2.substring(mMatcher.start());
								}
							}
						} else isVerse2 = false;
					} else isVerse2 = false;
				}

				if (isLogErr && !isVerse2) {
					String sVsNumber1 = Integer.toString(iVsNumber1);
					String sVerseOsisID1 = sChapterOsisID1 + "." + sVsNumber1;

					fosLogErr.write(sVerseOsisID1.getBytes("UTF-8"));
					fosLogErr.write(0x0A);

					isChapterWithErr = true;

				}

				if (!isLogErr) {
					// TODO speedup (введение ParChapter.putVerse прироста скорости не дало. Но пока на 2-х переводах!)
					returnChapter.putVerse(new Verse((iVsNumber2 - 1), sVerseText2), iVsNumber1);
				}

			}

			if (isLogErr && isChapterWithErr) {
				fosLogErr.write(0x0A);
			}

			return returnChapter;

		} catch (XPathExpressionException e) {

			// TODO заменить e.printStackTrace()

			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
	}


	public Chapter openParChapter(String toModuleID) throws BookNotFoundException, OpenModuleException {

		// Таблица версификации должна быть в корне модуля с именем файла "versmap.xml"
		// TODO Пока только обычне модули, не zip.
		final String VersificationFileName = "versmap.xml";

		FsModule fsModule1 = (FsModule) currModule;
		String VersificationFilePath = fsModule1.modulePath + File.separator + VersificationFileName;

		Document docVersificationMap = null;
		try {
			docVersificationMap = XmlUtil.fromXMLfile(VersificationFilePath);
		} catch (ParserConfigurationException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IOException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (SAXException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		ParModuleID = toModuleID;
		PreferenceHelper.saveStateString("ParModuleID", ParModuleID);

		Module ParModule = getModuleByID(ParModuleID);
		Book ParBook = getBookByID(ParModule, currBook.getID());

		ParChapter = ChapterVersification(currChapter, ParBook, docVersificationMap, null);

		if (ParChapter == null) {
			isShowParTranslates = false;
		} else {
			isShowParTranslates = (ParChapter.getVerseNumber().equals(currChapter.getVerseNumber()));
		}

		PreferenceHelper.saveStateBoolean("isShowParTranslates", isShowParTranslates);

		return ParChapter;
	}


	public void CheckVersificationMap(String toModuleID) throws BookNotFoundException, OpenModuleException {

		// Таблица версификации должна быть в корне модуля с именем файла "versmap.xml"
		// TODO Пока только обычне модули, не zip.
		final String VersificationFileName = "versmap.xml";
		final String LogErrFileName = "versmapErrors-utf8.txt";

		FsModule fsModule1 = (FsModule) currModule;
		String VersificationFilePath = fsModule1.modulePath + File.separator + VersificationFileName;
		String LogErrFilePath = fsModule1.modulePath + File.separator + LogErrFileName;

		Document docVersificationMap = null;
		try {
			docVersificationMap = XmlUtil.fromXMLfile(VersificationFilePath);
		} catch (ParserConfigurationException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IOException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (SAXException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		Module Module2 = getModuleByID(toModuleID);

		try {
			FileOutputStream fosLogErr = new FileOutputStream(LogErrFilePath);

			fosLogErr.write(0x0A);
			fosLogErr.write("=================================".getBytes("UTF-8"));
			fosLogErr.write(0x0A);
			//fosLogErr.write("From Current Module to KJV Module".getBytes("UTF-8"));
			fosLogErr.write("From Current Module to Selected Module".getBytes("UTF-8"));
			fosLogErr.write(0x0A);
			fosLogErr.write(0x0A);

			for (Book Book1 : getBookList(currModule)) {

				Book1 = getBookByID(currModule, Book1.getID());

				ArrayList<Chapter> arlChapters1 = new ArrayList<Chapter>();

				for (int iCh = 1; iCh <= Book1.chapterQty; iCh++) {
					arlChapters1.add(getChapterByNumber(Book1, iCh));
				}

				Book Book2 = getBookByID(Module2, Book1.getID());

				for (int iCh = 0; iCh < Book1.chapterQty; iCh++) {
					ChapterVersification(arlChapters1.get(iCh), Book2, docVersificationMap, fosLogErr);
				}
			}

			fosLogErr.write(0x0A);
			fosLogErr.write(0x0A);
			fosLogErr.write("=================================".getBytes("UTF-8"));
			fosLogErr.write(0x0A);
			//fosLogErr.write("From KJV Module to Current Module".getBytes("UTF-8"));
			fosLogErr.write("From Selected Module to Current Module".getBytes("UTF-8"));
			fosLogErr.write(0x0A);
			fosLogErr.write(0x0A);

			for (Book Book2 : getBookList(Module2)) {

				Book2 = getBookByID(Module2, Book2.getID());

				ArrayList<Chapter> arlChapters2 = new ArrayList<Chapter>();

				for (int iCh = 1; iCh <= Book2.chapterQty; iCh++) {
					arlChapters2.add(getChapterByNumber(Book2, iCh));
				}

				Book Book1 = getBookByID(currModule, Book2.getID());

				for (int iCh = 0; iCh < Book2.chapterQty; iCh++) {
					ChapterVersification(arlChapters2.get(iCh), Book1, docVersificationMap, fosLogErr);
				}
			}

			fosLogErr.write(0x0A);
			fosLogErr.write(0x0A);
			fosLogErr.write("=================================".getBytes("UTF-8"));
			fosLogErr.write(0x0A);
			fosLogErr.write("The End".getBytes("UTF-8"));
			fosLogErr.write(0x0A);

			fosLogErr.close();

		} catch (FileNotFoundException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IOException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (BookDefinitionException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (BooksDefinitionException e) {
			// TODO заменить e.printStackTrace()
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}


	///////////////////////////////////////////////////////////////////////////
	// NAVIGATION

	/**
	 * Возвращает список доступных модулей с Библиями, апокрифами, книгами
	 *
	 * @return возвращает ArrayList, содержащий модули с книгами Библии и апокрифами
	 */
	public ArrayList<ItemList> getModulesList() {
		// Сначала отсортируем список по наименованием модулей
		TreeMap<String, Module> tMap = new TreeMap<String, Module>();
		for (Module currModule : moduleCtrl.getModules().values()) {
			tMap.put(currModule.getName(), currModule);
		}

		// Теперь создадим результирующий список на основе отсортированных данных
		ArrayList<ItemList> moduleList = new ArrayList<ItemList>();
		for (Module currModule : tMap.values()) {
			moduleList.add(new ItemList(currModule.getID(), currModule.getName()));
		}

		return moduleList;
	}

	public LinkedList<ItemList> getHistoryList() {
		return historyManager.getLinks();
	}


	public ArrayList<ItemList> getModuleBooksList(String moduleID) throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		// Получим модуль по его ID
		Module module = moduleCtrl.getModuleByID(moduleID);
		ArrayList<ItemList> booksList = new ArrayList<ItemList>();
		for (Book book : bookCtrl.getBookList(module)) {
			booksList.add(new ItemList(book.getID(), book.name));
		}
		return booksList;
	}

	public ArrayList<ItemList> getCurrentModuleBooksList() throws OpenModuleException, BooksDefinitionException, BookDefinitionException {
		if (getCurrModule() == null) {
			return new ArrayList<ItemList>();
		}
		return this.getModuleBooksList(getCurrModule().getID());
	}

	/**
	 * Возвращает список глав книги
	 *
	 * @throws OpenModuleException
	 * @throws BookNotFoundException
	 */
	public ArrayList<String> getChaptersList(String moduleID, String bookID)
			throws BookNotFoundException, OpenModuleException {
		// Получим модуль по его ID
		Module module = getModule(moduleID);
		Book book = bookCtrl.getBookByID(module, bookID);
		return book.getChapterNumbers(module.ChapterZero);
	}

	private Module getModule(String moduleID) throws OpenModuleException {
		return moduleCtrl.getModuleByID(moduleID);
	}


	public void nextChapter() throws OpenModuleException {
		if (getCurrModule() == null || getCurrBook() == null) {
			return;
		}

		Integer chapterQty = getCurrBook().chapterQty;
		if (chapterQty > (getCurrChapterNumber() + (getCurrModule().ChapterZero ? 1 : 0))) {
			currChapterNumber = getCurrChapterNumber() + 1;
			currVerseNumber = 1;
		} else {
			try {
				ArrayList<Book> books = bookCtrl.getBookList(getCurrModule());
				int pos = books.indexOf(getCurrBook());
				if (++pos < books.size()) {
					currBook = books.get(pos);
					currChapterNumber = getCurrBook().getFirstChapterNumber();
					currVerseNumber = 1;
				}
			} catch (BooksDefinitionException e) {
				Log.e(TAG, e.getMessage());
			} catch (BookDefinitionException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	public void prevChapter() throws OpenModuleException {
		if (getCurrModule() == null || getCurrBook() == null) {
			return;
		}

		if (!getCurrChapterNumber().equals(getCurrBook().getFirstChapterNumber())) {
			currChapterNumber = getCurrChapterNumber() - 1;
			currVerseNumber = 1;
		} else {
			try {
				ArrayList<Book> books = bookCtrl.getBookList(getCurrModule());
				int pos = books.indexOf(getCurrBook());
				if (pos > 0) {
					currBook = books.get(--pos);
					Integer chapterQty = getCurrBook().chapterQty;
					currChapterNumber = chapterQty - (getCurrModule().ChapterZero ? 1 : 0);
					currVerseNumber = 1;
				}
			} catch (BooksDefinitionException e) {
				Log.e(TAG, e.getMessage());
			} catch (BookDefinitionException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}


	///////////////////////////////////////////////////////////////////////////
	// GET CONTENT

	public String getChapterHTMLView() {
		return chapterCtrl.getChapterHTMLView(getCurrChapter());
	}

	public String getParChapterHTMLView() {

		// TODO изменить на свойство в Librarian
		ArrayList<Chapter> alChapters = new ArrayList<Chapter>();

		alChapters.add(getCurrChapter());

		if (isShowParTranslates) {
			alChapters.add(getParChapter());
		}

		return chapterCtrl.getParChapterHTMLView(alChapters);
	}

	public Boolean isBible() {
		return getCurrModule() != null && getCurrModule().isBible;
	}


	///////////////////////////////////////////////////////////////////////////
	// SEARCH

	public LinkedHashMap<String, String> getSearchResults() {
		return this.searchResults;
	}

	public LinkedHashMap<String, String> search(String query, String fromBook, String toBook) throws OpenModuleException, BookNotFoundException {
		if (getCurrModule() == null) {
			searchResults = new LinkedHashMap<String, String>();
		} else {
			searchResults = bookCtrl.search(getCurrModule(), query, fromBook, toBook);
		}
		return searchResults;
	}


	///////////////////////////////////////////////////////////////////////////
	// GET LINK OF STRING

	public String getModuleFullName() {
		if (getCurrModule() == null) {
			return "";
		}
		return getCurrModule().getName();
	}

	public CharSequence getModuleName() {
		if (getCurrModule() == null) {
			return "";
		} else {
			return getCurrModule().getName();
		}
	}

	public String getModuleID() {
		if (getCurrModule() == null) {
			return "";
		} else {
			return getCurrModule().getID();
		}
	}

	public String getBookFullName(String moduleID, String bookID) throws OpenModuleException {
		// Получим модуль по его ID
		Module module;
		try {
			module = getModule(moduleID);
		} catch (OpenModuleException e) {
			return "---";
		}

		try {
			Book book = bookCtrl.getBookByID(module, bookID);
			return book.name;
		} catch (BookNotFoundException e) {
			return "---";
		}
	}

	public String getBookShortName(String moduleID, String bookID) throws OpenModuleException {
		// Получим модуль по его ID
		Module module;
		try {
			module = getModule(moduleID);
		} catch (OpenModuleException e) {
			return "---";
		}

		try {
			Book book = bookCtrl.getBookByID(module, bookID);
			return book.getShortName();
		} catch (BookNotFoundException e) {
			return "---";
		}
	}

	public BibleReference getCurrentOSISLink() {
		return new BibleReference(getCurrModule(), getCurrBook(), getCurrChapterNumber(), getCurrVerseNumber());
	}

	public void setCurrentVerseNumber(int verse) {
		this.currVerseNumber = verse;
	}

	public CharSequence getHumanBookLink() {
		if (getCurrBook() == null || getCurrChapter() == null) {
			return "";
		}
		String bookLink = getCurrBook().getShortName() + " " + getCurrChapter().getNumber();
		if (bookLink.length() > 10) {
			int strLenght = bookLink.length();
			bookLink = bookLink.substring(0, 4) + "..." + bookLink.substring(strLenght - 4, strLenght);
		}
		return bookLink;
	}

	public String getOSIStoHuman(String linkOSIS) throws BookNotFoundException, OpenModuleException {
		String[] param = linkOSIS.split("\\.");
		if (param.length < 3) {
			return "";
		}

		String moduleID = param[0];
		String bookID = param[1];
		String chapter = param[2];

		Module currModule;
		try {
			currModule = getModule(moduleID);
		} catch (OpenModuleException e) {
			return "";
		}
		Book currBook = bookCtrl.getBookByID(currModule, bookID);
		if (currBook == null) {
			return "";
		}
		String humanLink = moduleID + ": " + currBook.getShortName() + " " + chapter;
		if (param.length > 3) {
			humanLink += ":" + param[3];
		}

		return humanLink;
	}

	public String getHumanToOSIS(String humanLink) {
		// Получим имя модуля
		int position = humanLink.indexOf(":");
		if (position == -1) {
			return "";
		}
		String linkOSIS = humanLink.substring(0, position).trim();
		humanLink = humanLink.substring(position + 1).trim();
		if (humanLink.length() == 0) {
			return "";
		}

		// Получим имя книги
		position = humanLink.indexOf(" ");
		if (position == -1) {
			return "";
		}
		linkOSIS += "." + BibleBooksID.getID(humanLink.substring(0, position).trim());
		humanLink = humanLink.substring(position).trim();
		if (humanLink.length() == 0) {
			return linkOSIS + ".1";
		}

		// Получим номер главы
		position = humanLink.indexOf(":");
		if (position == -1) {
			return "";
		}
		linkOSIS += "." + humanLink.substring(0, position).trim().replaceAll("\\D", "");
		humanLink = humanLink.substring(position).trim().replaceAll("\\D", "");
		if (humanLink.length() == 0) {
			return linkOSIS;
		} else {
			// Оставшийся кусок - номер стиха
			return linkOSIS + "." + humanLink;
		}
	}

	public Boolean isOSISLinkValid(BibleReference link) {
		if (link.getPath() == null) {
			return false;
		}

		try {
			getModuleByID(link.getModuleID());
		} catch (OpenModuleException e) {
			return false;
		}
		return true;
	}


	///////////////////////////////////////////////////////////////////////////
	// SHARE

	public void shareText(Context context, TreeSet<Integer> selectVerses, Destination dest) {
		if (getCurrChapter() == null) {
			return;
		}

		LinkedHashMap<Integer, String> verses = getCurrChapter().getVerses(selectVerses);
		ShareBuilder builder = new ShareBuilder(context, getCurrModule(), getCurrBook(), getCurrChapter(), verses);
		builder.share(dest);
	}

	public String getBaseUrl() {
		if (getCurrModule() == null) {
			return "file:///url_initial_load";
		}
		String dataSourceID = getCurrModule().getDataSourceID();
		int pos = dataSourceID.lastIndexOf("/");
		if (++pos <= dataSourceID.length()) {
			return dataSourceID.substring(0, pos);
		} else {
			return dataSourceID;
		}
	}

	public void clearHistory() {
		historyManager.clearLinks();
	}

	public LinkedHashMap<String, BibleReference> getCrossReference(BibleReference bReference)
			throws TskNotFoundException, BQUniversalException {

		if (tskCtrl == null) {
			tskCtrl = new TSKController(new XmlTskRepository());
		}

		LinkedHashSet<BibleReference> csLinks = tskCtrl.getLinks(bReference);

		LinkedHashMap<String, BibleReference> parallels = new LinkedHashMap<String, BibleReference>();
		for (BibleReference reference : csLinks) {
			Book book;
			try {
				book = getBookByID(getCurrModule(), reference.getBookID());
			} catch (OpenModuleException e) {
				Log.e(TAG, String.format("Error open module %1$s for link %2$s",
						reference.getModuleID(), reference.getBookID()));
				continue;
			} catch (BookNotFoundException e) {
				Log.e(TAG, String.format("Not found book %1$s in module %2$s",
						reference.getBookID(), reference.getModuleID()));
				continue;
			}
			BibleReference newReference = new BibleReference(getCurrModule(), book,
					reference.getChapter(), reference.getFromVerse(), reference.getToVerse());
			parallels.put(
					LinkConverter.getOSIStoHuman(newReference, moduleCtrl, bookCtrl),
					newReference);
		}

		return parallels;
	}

	public HashMap<BibleReference, String> getCrossReferenceContent(Collection<BibleReference> bReferences) {
		HashMap<BibleReference, String> crossReferenceContent = new HashMap<BibleReference, String>();
		for (BibleReference ref : bReferences) {
			try {
				int fromVerse = ref.getFromVerse();
				int toVerse = ref.getToVerse();
				Chapter chapter = getChapterByNumber(getBookByID(getCurrModule(), ref.getBookID()), ref.getChapter());
				crossReferenceContent.put(ref,
						StringProc.stripTags(chapter.getText(fromVerse, toVerse))
								.replaceAll("\\s(H|G)*\\d+", "")
								.replaceAll("\\d+\\s", ""));
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return crossReferenceContent;
	}

	public Module getCurrModule() {
		return currModule;
	}

	public Book getCurrBook() {
		return currBook;
	}

	public Chapter getCurrChapter() {
		return currChapter;
	}

	public Chapter getParChapter() {
		return ParChapter;
	}

	public boolean isParChapter() {
		return (ParModuleID.length() != 0);
	}

	public void switchShowParTranslates() {
		isShowParTranslates = isParChapter() && !isShowParTranslates;
		PreferenceHelper.saveStateBoolean("isShowParTranslates", isShowParTranslates);
	}

	public Integer getCurrChapterNumber() {
        return currChapterNumber;
    }

	public Integer getCurrVerseNumber() {
		return currVerseNumber;
	}

	public ArrayList<String> getVersesText() {
		ArrayList<String> result = new ArrayList<String>();
		if (currChapter == null) return result;
		ArrayList<Verse> verses = currChapter.getVerseList();
		for (int i = 0; i < verses.size(); i++) {
			result.add(StringProc.cleanVerseText(verses.get(i).getText()));
		}
		return result;
	}

	public Locale getTextLocale() {
		return new Locale(currModule.getLanguage());
	}
}
