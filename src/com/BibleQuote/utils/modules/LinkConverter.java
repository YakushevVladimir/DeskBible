/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.utils.modules;

import com.BibleQuote.entity.BibleBooksID;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Book;
import com.BibleQuote.models.Module;
import com.BibleQuote.controllers.*;

public class LinkConverter {

	public static String getOSIStoHuman(String linkOSIS, IModuleController moduleCtrl,
			IBookController bookCtrl) throws BookNotFoundException,
			OpenModuleException {
		String[] param = linkOSIS.split("\\.");
		if (param.length < 3) {
			return "";
		}

		String moduleID = param[0];
		String bookID = param[1];
		String chapter = param[2];

		Module currModule;
		try {
			currModule = moduleCtrl.getModuleByID(moduleID);
		} catch (OpenModuleException e) {
			return "";
		}
		Book currBook = bookCtrl.getBookByID(currModule, bookID);
		if (currBook == null) {
			return "";
		}
		String humanLink = moduleID + ": " + currBook.getShortName() + " "
				+ chapter;
		if (param.length > 3) {
			humanLink += ":" + param[3];
		}

		return humanLink;
	}

	public static String getOSIStoHuman(BibleReference reference, IModuleController moduleCtrl,
			IBookController bookCtrl) {
		if (reference.getFromVerse() != reference.getToVerse()) {
			return String.format("%1$s %2$s:%3$s-%4$s", 
					reference.getBookFullName(), reference.getChapter(), 
					reference.getFromVerse(), reference.getToVerse());
		} else {
			return String.format("%1$s %2$s:%3$s", 
					reference.getBookFullName(), reference.getChapter(), reference.getFromVerse());
		}
	}

	public static String getHumanToOSIS(String humanLink) {
		String linkOSIS = "";

		// Получим имя модуля
		int position = humanLink.indexOf(":");
		if (position == -1) {
			return "";
		}
		linkOSIS = humanLink.substring(0, position).trim();
		humanLink = humanLink.substring(position + 1).trim();
		if (humanLink.length() == 0) {
			return "";
		}

		// Получим имя книги
		position = humanLink.indexOf(" ");
		if (position == -1) {
			return "";
		}
		linkOSIS += "."
				+ BibleBooksID.getID(humanLink.substring(0, position).trim());
		humanLink = humanLink.substring(position).trim();
		if (humanLink.length() == 0) {
			return linkOSIS + ".1";
		}

		// Получим номер главы
		position = humanLink.indexOf(":");
		if (position == -1) {
			return "";
		}
		linkOSIS += "."
				+ humanLink.substring(0, position).trim().replaceAll("\\D", "");
		humanLink = humanLink.substring(position).trim().replaceAll("\\D", "");
		if (humanLink.length() == 0) {
			return linkOSIS;
		} else {
			// Оставшийся кусок - номер стиха
			return linkOSIS + "." + humanLink;
		}
	}

}
