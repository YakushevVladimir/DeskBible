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
package com.BibleQuote.modules;

import com.BibleQuote.exceptions.FileAccessException;
import com.BibleQuote.utils.FsUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Yakushev Vladimir, Sergey Ursul
 */
public class FsModule extends Module {

	private static final long serialVersionUID = -660821372799486761L;

	/**
	 * modulePath is a directory path or an archive path with a name
	 */
	public final String modulePath;
	public final String versificationFileName;

	/**
	 * Имя ini-файла (раскладка в названии файла может быть произвольной)
	 */
	public final String iniFileName;

	public final Boolean isArchive;

	public FsModule(String iniFilePath) {
		modulePath = iniFilePath.substring(0, iniFilePath.lastIndexOf(File.separator));
		iniFileName = iniFilePath.substring(iniFilePath.lastIndexOf(File.separator) + 1);
		isArchive = modulePath.toLowerCase().endsWith(".zip");


		// Таблица версификации должна быть в корне модуля (там же, где и bibleqt.ini) с именем файла "versmap*.xml",
		// такой файл должен быть только один.

		if (isArchive) {

			boolean isZipVersMap = false;
			String sZipVersMapName = null;

			File zipFile = new File(modulePath);
			try {
				InputStream moduleStream = new FileInputStream(zipFile);
				ZipInputStream zStream = new ZipInputStream(moduleStream);
				ZipEntry entry;
				while ((entry = zStream.getNextEntry()) != null) {
					String entryName = entry.getName().toLowerCase();

					// файл "versmap*.xml" должен быть только там же, где и bibleqt.ini
					//if (entryName.contains(File.separator)) {
					//	entryName = entryName.substring(entryName.lastIndexOf(File.separator) + 1);
					//}

					if (entryName.startsWith("versmap") && entryName.endsWith(".xml")) {
						isZipVersMap = !isZipVersMap;

						// файл "versmap*.xml" должен быть только один
						if (!isZipVersMap) {
							break;
						}

						sZipVersMapName = entryName;
					}
				}

			} catch (FileNotFoundException e) {
				isZipVersMap = false;

				// TODO заменить e.printStackTrace()
				//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (IOException e) {
				isZipVersMap = false;

				// TODO заменить e.printStackTrace()
				//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}


			if (isZipVersMap) {
				versificationFileName = sZipVersMapName;
			} else {
				versificationFileName = null;
			}


		} else { //  !isArchive

			FilenameFilter fnfVersMap = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					name = name.toLowerCase();
					return name.startsWith("versmap") && name.endsWith(".xml");
				}
			};

			File dirModule = new File(modulePath);
			String[] saModuleFileNames = dirModule.list(fnfVersMap);

			// файл "versmap*.xml" должен быть только один
			if (saModuleFileNames.length == 1) {
				versificationFileName = saModuleFileNames[0];
			} else {
				versificationFileName = null;
			}
		}


		versificationMap = null;
	}

	@Override
	public String getDataSourceID() {
		return this.modulePath + File.separator + this.iniFileName;
	}

	@Override
	public String getID() {
		return ShortName.toUpperCase();
	}

	@Override
	public VersificationMap getVersificationMap() {

		if (versificationMap == null) {

			BufferedReader brVersificationFile = null;

			if (versificationFileName != null) {
				try {

					if (isArchive) {

						brVersificationFile = FsUtils.getTextFileReaderFromZipArchive(
								  modulePath, versificationFileName, "UTF-8");

					} else {

						brVersificationFile = new BufferedReader(new FileReader(modulePath + File.separator
								  + versificationFileName));
					}

				} catch (FileNotFoundException e) {
					brVersificationFile = null;
					// TODO заменить e.printStackTrace()
					//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				} catch (FileAccessException e) {
					brVersificationFile = null;
					// TODO заменить e.printStackTrace()
					//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}

			versificationMap = new VersificationMap(brVersificationFile);
		}

		return versificationMap;
	}

}
