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
package com.BibleQuote.utils;

import android.content.Context;
import android.os.Environment;
import com.BibleQuote.BibleQuoteApp;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Класс отвечающий за запись протокола событий приложения.
 * Записывает отладочную информация в файл log.txt,
 * находящийся в корне съемного диска устройства
 *
 * @author Владимир Якушев (ru.phoenix@gmail.com)
 */
public class Log {
	static private File logFile = null;

	/**
	 * Подготовка файла-протокола событий. Создание нового файла,
	 * запись текущей даты, версии программы, языка системы
	 *
	 * @param context
	 */
	public static void Init(Context context) {

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			logFile = new File(DataConstants.FS_APP_DIR_NAME, "log.txt");
			if (logFile.exists()) {
				logFile.delete();
			}

			Write("Log " + new SimpleDateFormat("dd-MMM-yy G hh:mm aaa").format(Calendar.getInstance().getTime()));
			Write("Current version package: " + BibleQuoteApp.getAppVersionName(context));
			Write("Default language: " + Locale.getDefault().getDisplayLanguage());
			Write("Device model: " + android.os.Build.BRAND + " " + android.os.Build.MODEL);
			Write("Device display: " + android.os.Build.BRAND + " " + android.os.Build.DISPLAY);
			Write("Android OS: " + android.os.Build.VERSION.RELEASE);
			Write("====================================");
		}
	}

	/**
	 * Запись в протокол события
	 *
	 * @param Tag  имя класса-инициатора события
	 * @param text текст помещаемый в протокол событий
	 */
	private static void Write(String Tag, String text) {
		if (logFile == null) {
			return;
		}

		BufferedWriter bWriter = GetWriter();
		if (bWriter == null) {
			return;
		}

		try {
			bWriter.write((Tag != null ? Tag + ": " : "") + text + "\r\n");
			bWriter.flush();
			bWriter.close();
		} catch (IOException e) {
			return;
		}
	}

	/**
	 * Запись в протокол событий сообщения об ошибке
	 *
	 * @param Tag  имя класса-инициатора события
	 * @param text текст помещаемый в протокол событий
	 * @param e    ссылка на полученный Exception
	 */
	public static void e(String Tag, String text, Exception e) {
		Write(Tag, String.format("Error: $1$s\r\nMessage: %2$s", text, e.getMessage()));
	}

	public static void e(String Tag, String text) {
		Write(Tag, "Error: " + text);
	}

	/**
	 * Запись в протокол событий информационного сообщения
	 *
	 * @param Tag  имя класса-инициатора события
	 * @param info текст помещаемый в протокол событий
	 */
	public static void i(String Tag, String info) {
		Write(Tag, info);
	}

	private static void Write(String text) {
		Write(null, text);
	}

	private static BufferedWriter GetWriter() {
		try {
			OutputStreamWriter oWriter = new OutputStreamWriter(new FileOutputStream(logFile, true));
			BufferedWriter bWriter = new BufferedWriter(oWriter);
			return bWriter;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
