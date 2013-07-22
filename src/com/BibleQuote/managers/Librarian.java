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
import android.content.Intent;
import com.BibleQuote.ui.ServiceActivity;
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
import com.BibleQuote.utils.modules.LinkConverter;

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

	private String ParModuleID = "";
	private ChapterQueueList chapterQueueList;

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


	public EtalonChapter getEtalonChapter(Chapter chapter, VersificationMap versificationMap) {

		int iChapNumber1 = chapter.getNumber();
		//String sChapNumber1 = Integer.toString(iChapNumber1);

		String sBookOsisID1 = chapter.getBook().OSIS_ID;
		//String sChapterOsisID1 = sBookOsisID1 + "." + sChapNumber1;

		int iChapNumber2 = iChapNumber1;
		//String sChapNumber2 = sChapNumber1;


		EtalonChapter etalonChapter = new EtalonChapter();
		etalonChapter.Book_OSIS_ID = sBookOsisID1;


		for (int iVsNumber1 = 1; iVsNumber1 <= chapter.size(); iVsNumber1++) {

			//String sVsNumber1 = Integer.toString(iVsNumber1);
			//String sVsNumber2 = sVsNumber1;
			//String sVerseOsisID1 = sChapterOsisID1 + "." + sVsNumber1;
			//String sBookOsisID2 = sBookOsisID1;
			//String sChapterOsisID2 = sChapterOsisID1;
			//String sVerseOsisID2 = sVerseOsisID1;


			VersificationMap.VerseDifferences verseDiffs =
					versificationMap.getMapForVerse(true, sBookOsisID1, iChapNumber1, iVsNumber1);


			iChapNumber2 = iChapNumber1 + verseDiffs.iDifCh;

			// исходящий стих в эталоне не повторяем, если только это не первый стих в главе
			int iSeqVerse = (verseDiffs.iVsRepeated != 0 && iVsNumber1 != 1) ? 1 : 0;
			int iSeqEnd = (verseDiffs.iCountSequence == 0 || verseDiffs.iCountSequence == 1) ? 1 : verseDiffs.iCountSequence;
			int iVsNumber2 = iVsNumber1 + verseDiffs.iDifVs + iSeqVerse;
			//sVsNumber2 = Integer.toString(iVsNumber2);
			//sVerseOsisID2 = sChapterOsisID2 + "." + sVsNumber2;

			while (iSeqVerse < iSeqEnd) {

				if (verseDiffs.iNextChapter > 1 && iSeqVerse == verseDiffs.iNextChapter - 1) {
					iChapNumber2++;
					iVsNumber2 = 1;
				}

				etalonChapter.put(iChapNumber2, iVsNumber2);

				iVsNumber2++;
				iSeqVerse++;
			}
		}


		return etalonChapter;
	}


	public ChapterQueue getChapterQueueFromEtalon(EtalonChapter etalonChapter, Book toBook,
												  VersificationMap versificationMap, FileOutputStream fosVersMapLogErr)
			throws BookNotFoundException {


		try {

			String sBookOsisID1 = etalonChapter.Book_OSIS_ID;
			Book Book2 = toBook;

			String sModuleShortNameWithDot = (PreferenceHelper.showShortNameInParTrans()) ? Book2.getModule().ShortName + "." : "";
			ChapterQueue chapterQueue = new ChapterQueue(Book2);
			boolean isChapterWithErr = false;

			boolean isCheckingVersMap = (fosVersMapLogErr != null);

			int iChapNumber1_old = 0;
			int iChapNumber2_old = 0;

			String sChapNumber1 = "";
			//String sChapterOsisID1 = "";

			int iChapNumber2 = 0;
			String sChapNumber2 = "";
			Chapter Chapter2 = null;


			for (int iEtVerse = 0; iEtVerse < etalonChapter.size(); iEtVerse++) {

				EtalonVerse etalonVerse = etalonChapter.get(iEtVerse);

				int iChapNumber1 = etalonVerse.iChapterNumber;
				int iVsNumber1 = etalonVerse.iVerseNumber;
				boolean isNewChapter1 = (iChapNumber1_old != iChapNumber1);

				if (isNewChapter1) {
					sChapNumber1 = Integer.toString(iChapNumber1);
					//sChapterOsisID1 = sBookOsisID1 + "." + sChapNumber1;
				}


				//String sVsNumber1 = Integer.toString(iVsNumber1);
				//String sVsNumber2 = sVsNumber1;
				//String sVerseOsisID1 = sChapterOsisID1 + "." + sVsNumber1;
				//String sBookOsisID2 = sBookOsisID1;
				//String sChapterOsisID2 = sChapterOsisID1;
				//String sVerseOsisID2 = sVerseOsisID1;


				VersificationMap.VerseDifferences verseDiffs =
						versificationMap.getMapForVerse(false, sBookOsisID1, iChapNumber1, iVsNumber1);


				iChapNumber2 = iChapNumber1 + verseDiffs.iDifCh;

				boolean isNewChapter2 = (iChapNumber2_old != iChapNumber2);

				if (isNewChapter2) {
					sChapNumber2 = Integer.toString(iChapNumber2);

					if (Book2 != null) {
						Chapter2 = getChapterByNumber(Book2, iChapNumber2);
					}
				}


				//sBookOsisID2 = sIntoBook;
				//sChapterOsisID2 = sBookOsisID2 + "." + sChapNumber2;


				int iSequenceFlags = 0;

				if (verseDiffs.iVsRepeated != 0) {
					iSequenceFlags = iSequenceFlags | VerseQueue.SEQ_REPEATED;
				}

				if (verseDiffs.iCountSequence != 0) {
					iSequenceFlags = iSequenceFlags | VerseQueue.SEQ_SEQUENCED;
				}

				if (iSequenceFlags == 0) {
					iSequenceFlags = VerseQueue.SEQ_NORMAL;
				}


				//int iSeqVerse = (verseDiffs.iVsRepeated != 0 && iVsNumber1 != 1) ? 1 : 0;  -- при отображении повтор стиха иногда нужен (по строкам)
				int iSeqVerse = 0;
				int iSeqEnd = (verseDiffs.iCountSequence == 0 || verseDiffs.iCountSequence == 1) ? 1 : verseDiffs.iCountSequence;
				int iVsNumber2 = iVsNumber1 + verseDiffs.iDifVs;  // + iSeqVerse;  -- iSeqVerse == 0
				//sVsNumber2 = Integer.toString(iVsNumber2);
				//sVerseOsisID2 = sChapterOsisID2 + "." + sVsNumber2;


				while (iSeqVerse < iSeqEnd) {

					if (verseDiffs.iNextChapter > 1 && iSeqVerse == verseDiffs.iNextChapter - 1) {
						iChapNumber2++;
						iVsNumber2 = 1;

						sChapNumber2 = Integer.toString(iChapNumber2);

						if (Book2 != null) {
							Chapter2 = getChapterByNumber(Book2, iChapNumber2);
						}
					}


					String sVerseText2 = "---";
					boolean isVerse2 = true;


					if (Chapter2 != null) {

						// speedup (введение Chapter2 прироста скорости не дал)
						Verse vsVerse2 = Chapter2.getVerse(iVsNumber2);

						if (vsVerse2 != null) {

							if (!isCheckingVersMap) {
								sVerseText2 = vsVerse2.getText();

								// speedup (вынос Pattern.compile("\\d") за цикл прироста не дал)
								Matcher mMatcher = Pattern.compile("\\d").matcher(sVerseText2);

								if (mMatcher.find()) {

									// speedup (StringBuilder вместо "+" прироста скорости не дал)
									sVerseText2 = sVerseText2.substring(0, mMatcher.start())
											+ sModuleShortNameWithDot + sChapNumber2 + "." + sVerseText2.substring(mMatcher.start());
								}
							}
						} else isVerse2 = false;
					} else isVerse2 = false;


					if (!isCheckingVersMap) {
						VerseQueue verseQueue =
								new VerseQueue(iChapNumber2, (iVsNumber2 - 1), sVerseText2, iSequenceFlags);

						chapterQueue.offer(verseQueue);
					}


					if (isCheckingVersMap && !isVerse2) {
						String sVsNumber1 = Integer.toString(iVsNumber1);
						String sVsNumber2 = Integer.toString(iVsNumber2);

						String sErrVerse = "Etalon." + Book2.OSIS_ID + "." + sChapNumber1 + "." + sVsNumber1 + " -- " +
								Book2.getModule().ShortName + "." + Book2.OSIS_ID
								+ "." + sChapNumber2 + "." + sVsNumber2;

						fosVersMapLogErr.write(sErrVerse.getBytes("UTF-8"));
						fosVersMapLogErr.write(0x0A);

						isChapterWithErr = true;

					}


					iSequenceFlags = 0;
					if (verseDiffs.iCountSequence != 0) {
						iSequenceFlags = iSequenceFlags | VerseQueue.SEQ_SEQUENCED;
					}

					if (iSequenceFlags == 0) {
						iSequenceFlags = VerseQueue.SEQ_NORMAL;
					}


					iVsNumber2++;
					iSeqVerse++;
				}

				iChapNumber1_old = iChapNumber1;
				iChapNumber2_old = iChapNumber2;
			}


			if (isCheckingVersMap && isChapterWithErr) {
				fosVersMapLogErr.write(0x0A);
			}


			return chapterQueue;


		} catch (UnsupportedEncodingException e) {
			// TODO заменить e.printStackTrace()
			//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		} catch (IOException e) {
			// TODO заменить e.printStackTrace()
			//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return null;
		}
	}


	public ChapterQueueList openParChapter(String toModuleID) throws BookNotFoundException, OpenModuleException {

		ParModuleID = toModuleID;
		PreferenceHelper.saveStateString("ParModuleID", ParModuleID);

		EtalonChapter etalonChapter = getEtalonChapter(currChapter, currModule.getVersificationMap());

		ChapterQueue chapterQueue_1 = getChapterQueueFromEtalon(etalonChapter, currBook,
				  currModule.getVersificationMap(), null);


		Module ParModule = getModuleByID(ParModuleID);
		Book ParBook = getBookByID(ParModule, currBook.getID());

		ChapterQueue chapterQueue_2 = getChapterQueueFromEtalon(etalonChapter, ParBook,
				  ParModule.getVersificationMap(), null);


		chapterQueueList = new ChapterQueueList();

		if (chapterQueue_1 != null) {
			chapterQueueList.add(chapterQueue_1);
		}

		if (chapterQueue_2 != null) {
			chapterQueueList.add(chapterQueue_2);
		}


		isShowParTranslates = !chapterQueueList.isEmpty();

		PreferenceHelper.saveStateBoolean("isShowParTranslates", isShowParTranslates);


		// Восстанавливаем контекст LibraryController (FsBookRepository.context.bookSet) относительно currModule
		// и делаем currBook соответствующей этому контексту
		currBook = getBookByID(currModule, currBook.getID());


		return chapterQueueList;
	}


	public void CheckVersificationMap(String toModuleID) throws BookNotFoundException, OpenModuleException {

		try {

			long lTime_start = System.currentTimeMillis();


			Module Module1 = currModule;
			Module Module2 = getModuleByID(toModuleID);

			String LogErrFileName = "versmapErrors_" + Module1.ShortName + "_" + Module2.ShortName + ".txt";

			FsModule fsModule1 = (FsModule) Module1;

			String sDirOfModules = fsModule1.modulePath.substring(0, fsModule1.modulePath.lastIndexOf(File.separator));
			String LogErrFilePath = sDirOfModules + File.separator + LogErrFileName;


			FileOutputStream fosLogErr = new FileOutputStream(LogErrFilePath);

			fosLogErr.write(0x0A);
			fosLogErr.write("=================================".getBytes("UTF-8"));
			fosLogErr.write(0x0A);

			String sHeader = "From " + Module1.ShortName + " Module to " + Module2.ShortName + " Module";

			fosLogErr.write(sHeader.getBytes("UTF-8"));
			fosLogErr.write(0x0A);
			fosLogErr.write(0x0A);


			ArrayList<Book> bookList = getBookList(Module1);

			for (int iBookNumber = 0; iBookNumber < bookList.size(); iBookNumber++) {
				Book book = bookList.get(iBookNumber);

				if (Thread.interrupted()) return;

				Intent intentStatusInfo = new Intent(ServiceActivity.BROADCAST_ACTION)
						  .putExtra(ServiceActivity.STATUS_MSG, ServiceActivity.STATUS_INFO)
						  .putExtra(ServiceActivity.PASS_NUMBER, 1)
						  .putExtra(ServiceActivity.FROM_MODULE_ID, Module1.ShortName)
						  .putExtra(ServiceActivity.TO_MODULE_ID, Module2.ShortName)
						  .putExtra(ServiceActivity.BOOK_ID, book.getID())
						  .putExtra(ServiceActivity.BOOK_NUMBER, iBookNumber + 1)
						  .putExtra(ServiceActivity.BOOKS_QTY, bookList.size());
				libCtrl.getUnit().getLibraryContext().getContext().sendBroadcast(intentStatusInfo);


				ArrayList<EtalonChapter> arlEtalonChapters = new ArrayList<EtalonChapter>(70);

				Book Book1 = null;
				try {
					// перед getChapterByNumber() должно быть getBookByID(),
					// т.к. после вызова getBookByID() для второго модуля меняется контекст LibraryController
					// (FsBookRepository.context.bookSet)
					Book1 = getBookByID(Module1, book.getID());

					for (int iCh = 1; iCh <= Book1.chapterQty; iCh++) {
						if (Thread.interrupted()) return;
						arlEtalonChapters.add(getEtalonChapter(getChapterByNumber(Book1, iCh), Module1.getVersificationMap()));
					}


					if (!Module1.getVersificationMap().isEmpty()) {

						// for Book1 from Etalon
						for (int iCh = 0; iCh < arlEtalonChapters.size(); iCh++) {
							if (Thread.interrupted()) return;
							getChapterQueueFromEtalon(arlEtalonChapters.get(iCh), Book1, Module1.getVersificationMap(), fosLogErr);
						}
					}

				} catch (BookNotFoundException e) {
					String sMessage = "Book " + book.getID() + " in module " + Module1.getID() + " not found";
					fosLogErr.write(sMessage.getBytes("UTF-8"));
					fosLogErr.write(0x0A);
					//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}


				Book Book2 = null;
				try {
					Book2 = getBookByID(Module2, book.getID());

					// for Book2 from Etalon
					for (int iCh = 0; iCh < arlEtalonChapters.size(); iCh++) {
						if (Thread.interrupted()) return;
						getChapterQueueFromEtalon(arlEtalonChapters.get(iCh), Book2, Module2.getVersificationMap(), fosLogErr);
					}
				} catch (BookNotFoundException e) {
					String sMessage = "Book " + book.getID() + " in module " + Module2.getID() + " not found";
					fosLogErr.write(sMessage.getBytes("UTF-8"));
					fosLogErr.write(0x0A);
					//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}


			fosLogErr.write(0x0A);
			fosLogErr.write(0x0A);
			fosLogErr.write("=================================".getBytes("UTF-8"));
			fosLogErr.write(0x0A);

			sHeader = "From " + Module2.ShortName + " Module to " + Module1.ShortName + " Module";

			fosLogErr.write(sHeader.getBytes("UTF-8"));
			fosLogErr.write(0x0A);
			fosLogErr.write(0x0A);


			for (int iBookNumber = 0; iBookNumber < bookList.size(); iBookNumber++) {
				Book book = bookList.get(iBookNumber);

				if (Thread.interrupted()) return;

				Intent intentStatusInfo = new Intent(ServiceActivity.BROADCAST_ACTION)
						  .putExtra(ServiceActivity.STATUS_MSG, ServiceActivity.STATUS_INFO)
						  .putExtra(ServiceActivity.PASS_NUMBER, 2)
						  .putExtra(ServiceActivity.FROM_MODULE_ID, Module2.ShortName)
						  .putExtra(ServiceActivity.TO_MODULE_ID, Module1.ShortName)
						  .putExtra(ServiceActivity.BOOK_ID, book.getID())
						  .putExtra(ServiceActivity.BOOK_NUMBER, iBookNumber + 1)
						  .putExtra(ServiceActivity.BOOKS_QTY, bookList.size());
				libCtrl.getUnit().getLibraryContext().getContext().sendBroadcast(intentStatusInfo);


				ArrayList<EtalonChapter> arlEtalonChapters = new ArrayList<EtalonChapter>(70);

				Book Book2 = null;
				try {
					// перед getChapterByNumber() должно быть getBookByID(),
					// т.к. после вызова getBookByID() для второго модуля меняется контекст LibraryController
					// (FsBookRepository.context.bookSet)
					Book2 = getBookByID(Module2, book.getID());

					for (int iCh = 1; iCh <= Book2.chapterQty; iCh++) {
						if (Thread.interrupted()) return;
						arlEtalonChapters.add(getEtalonChapter(getChapterByNumber(Book2, iCh), Module2.getVersificationMap()));
					}


					if (!Module2.getVersificationMap().isEmpty()) {

						// for Book2 from Etalon
						for (int iCh = 0; iCh < arlEtalonChapters.size(); iCh++) {
							if (Thread.interrupted()) return;
							getChapterQueueFromEtalon(arlEtalonChapters.get(iCh), Book2, Module2.getVersificationMap(), fosLogErr);
						}
					}

				} catch (BookNotFoundException e) {
					String sMessage = "Book " + book.getID() + " in module " + Module2.getID() + " not found";
					fosLogErr.write(sMessage.getBytes("UTF-8"));
					fosLogErr.write(0x0A);
					//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}


				Book Book1 = null;
				try {
					Book1 = getBookByID(Module1, book.getID());

					// for Book1 from Etalon
					for (int iCh = 0; iCh < arlEtalonChapters.size(); iCh++) {
						if (Thread.interrupted()) return;
						getChapterQueueFromEtalon(arlEtalonChapters.get(iCh), Book1, Module1.getVersificationMap(), fosLogErr);
					}

				} catch (BookNotFoundException e) {
					String sMessage = "Book " + book.getID() + " in module " + Module1.getID() + " not found";
					fosLogErr.write(sMessage.getBytes("UTF-8"));
					fosLogErr.write(0x0A);
					//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}


			long lTimeDelta = System.currentTimeMillis() - lTime_start;
			String sTime = "TimeDelta from start = " + lTimeDelta + " milliseconds";

			fosLogErr.write(0x0A);
			fosLogErr.write(0x0A);
			fosLogErr.write(sTime.getBytes("UTF-8"));
			fosLogErr.write(0x0A);


			fosLogErr.write("=================================".getBytes("UTF-8"));
			fosLogErr.write(0x0A);
			fosLogErr.write("The End".getBytes("UTF-8"));
			fosLogErr.write(0x0A);

			fosLogErr.close();

		} catch (FileNotFoundException e) {
			// TODO заменить e.printStackTrace()
			//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IOException e) {
			// TODO заменить e.printStackTrace()
			//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (BookDefinitionException e) {
			// TODO заменить e.printStackTrace()
			//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (BooksDefinitionException e) {
			// TODO заменить e.printStackTrace()
			//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		// Восстанавливаем контекст LibraryController (FsBookRepository.context.bookSet) относительно currModule
		// и делаем currBook соответствующей этому контексту
		currBook = getBookByID(currModule, currBook.getID());

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
		if (isShowParTranslates) {
			return chapterCtrl.getParChapterHTMLView(null, chapterQueueList);
		} else {
			return chapterCtrl.getParChapterHTMLView(getCurrChapter(), null);
		}
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
