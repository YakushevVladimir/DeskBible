/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: Logger.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.utils;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.BibleQuote.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Класс отвечающий за запись протокола событий приложения.
 * Записывает отладочную информация в файл log.txt,
 * находящийся в корне съемного диска устройства
 *
 * @author Владимир Якушев (ru.phoenix@gmail.com)
 */
public final class Logger {

    private static final String TAG = Logger.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    private static File logFile;

    private Logger() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    /**
     * Запись в протокол событий отладочного сообщения
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    public static void d(Object tag, String message) {
        Log.d(getTag(tag), message);
        if (BuildConfig.DEBUG) {
            write(getTag(tag), message);
        }
    }

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag  имя класса-инициатора события
     * @param text текст помещаемый в протокол событий
     */
    public static void e(Object tag, String text) {
        Log.e(getTag(tag), text);
        write(getTag(tag), "Error: " + text);
    }

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag  имя класса-инициатора события
     * @param text текст помещаемый в протокол событий
     * @param e    ссылка на полученный Exception
     */
    public static void e(Object tag, String text, Exception e) {
        Log.e(getTag(tag), text, e);
        write(getTag(tag), String.format("Error: %s%n%s", text, Log.getStackTraceString(e)));
    }

    /**
     * Запись в протокол событий информационного сообщения
     *
     * @param tag  имя класса-инициатора события
     * @param info текст помещаемый в протокол событий
     */
    public static void i(Object tag, String info) {
        Log.i(getTag(tag), info);
        write(getTag(tag), info);
    }

    private static String getTag(Object src) {
        if (src instanceof String) {
            return (String) src;
        } else {
            return src.getClass().getSimpleName();
        }
    }

    /**
     * Подготовка файла-протокола событий. Создание нового файла,
     * запись текущей даты, версии программы, языка системы
     */
    private static File getLogFile() {
        if (logFile == null && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            logFile = new File(DataConstants.getFsAppDirName(), "log.txt");
            if (logFile.exists() && !logFile.canWrite()) {
                return null;
            }

            write("====================================");
            write("Application version: " + BuildConfig.VERSION_NAME);
            write("Default language: " + Locale.getDefault().getDisplayLanguage());
            write("Device model: " + Build.MODEL);
            write("Android OS: " + Build.VERSION.RELEASE);
            write("------------------------------------");
        }
        return logFile;
    }

    private static void write(String text) {
        write(null, text);
    }

    /**
     * Запись в протокол события
     *
     * @param tag  имя класса-инициатора события
     * @param text текст помещаемый в протокол событий
     */
    private static void write(String tag, String text) {
        File log = getLogFile();
        if (log == null) {
            return;
        }

        try (
                OutputStreamWriter oWriter = new OutputStreamWriter(new FileOutputStream(log, true), Charset.forName("UTF-8"));
                BufferedWriter writer = new BufferedWriter(oWriter)
        ) {
            writer.write(String.format("%s %s %s%n", dateFormat.format(new Date()), tag != null ? tag : TAG, text));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
