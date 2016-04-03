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

import android.os.Environment;
import com.BibleQuote.BuildConfig;

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
public final class Log {
	static private File logFile;

	private Log() throws InstantiationException {
		throw new InstantiationException("This class is not for instantiation");
	}

	/**
	 * Подготовка файла-протокола событий. Создание нового файла,
	 * запись текущей даты, версии программы, языка системы
	 *
	 */
	public static void init() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			logFile = new File(DataConstants.FS_APP_DIR_NAME, "log.txt");
			if (logFile.exists()) {
				logFile.delete();
			}

			write("Log " + new SimpleDateFormat("dd-MMM-yy G hh:mm aaa").format(Calendar.getInstance().getTime()));
			write("Current version package: " + BuildConfig.VERSION_NAME);
			write("Default language: " + Locale.getDefault().getDisplayLanguage());
			write("Device model: " + android.os.Build.BRAND + " " + android.os.Build.MODEL);
			write("Device display: " + android.os.Build.BRAND + " " + android.os.Build.DISPLAY);
			write("Android OS: " + android.os.Build.VERSION.RELEASE);
			write("====================================");
		}
	}

	/**
	 * Запись в протокол события
	 *
	 * @param tag  имя класса-инициатора события
	 * @param text текст помещаемый в протокол событий
	 */
	private static void write(String tag, String text) {
		if (logFile == null) {
			return;
		}

		BufferedWriter bWriter = getWriter();
		if (bWriter == null) {
			return;
		}

		try {
			bWriter.write((tag != null ? tag + ": " : "") + text + "\r\n");
			bWriter.flush();
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Запись в протокол событий сообщения об ошибке
	 *
	 * @param tag  имя класса-инициатора события
	 * @param text текст помещаемый в протокол событий
	 * @param e    ссылка на полученный Exception
	 */
	public static void e(String tag, String text, Exception e) {
		write(tag, String.format("Error: $1$s\r\nMessage: %2$s", text, e.getMessage()));
	}

	public static void e(String tag, String text) {
		write(tag, "Error: " + text);
	}

	/**
	 * Запись в протокол событий информационного сообщения
	 *
	 * @param tag  имя класса-инициатора события
	 * @param info текст помещаемый в протокол событий
	 */
	public static void i(String tag, String info) {
		write(tag, info);
	}

	private static void write(String text) {
		write(null, text);
	}

	private static BufferedWriter getWriter() {
		try {
			OutputStreamWriter oWriter = new OutputStreamWriter(new FileOutputStream(logFile, true));
			return new BufferedWriter(oWriter);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
