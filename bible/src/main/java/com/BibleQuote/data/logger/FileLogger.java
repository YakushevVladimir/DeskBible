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
 * File: FileLogger.java
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.data.logger;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.BibleQuote.BuildConfig;
import com.BibleQuote.domain.logger.Logger;
import com.BibleQuote.utils.DataConstants;

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
public final class FileLogger extends Logger {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final long MAX_LOG_SIZE = 10 * 1024 * 1024;
    private static final String TAG = FileLogger.class.getSimpleName();
    private File logFile;

    public FileLogger() {
        createLogFile();
    }

    @Override
    public void debug(Object tag, String message) {
        if (BuildConfig.DEBUG) {
            write(getTag(tag), message);
        }
    }

    @Override
    public void error(Object tag, String message) {
        write(getTag(tag), "Error: " + message);
    }

    @Override
    public void error(Object tag, String message, Throwable th) {
        write(getTag(tag), String.format("Error: %s%n%s", message, Log.getStackTraceString(th)));
    }

    @Override
    public void info(Object tag, String message) {
        write(getTag(tag), message);
    }

    /**
     * Подготовка файла-протокола событий. Создание нового файла,
     * запись текущей даты, версии программы, языка системы
     */
    private void createLogFile() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return;
        }

        String tag = getTag(this);
        logFile = new File(DataConstants.getFsAppDirName(), "log.txt");
        if (logFile.exists() && !logFile.canWrite()) {
            return;
        } else if (logFile.length() > MAX_LOG_SIZE) {
            if (!logFile.delete()) {
                write(tag, "Не удалось очистить лог-файл");
            }
        }

        write(tag, "====================================");
        write(tag, "Application version: " + BuildConfig.VERSION_NAME);
        write(tag, "Default language: " + Locale.getDefault().getDisplayLanguage());
        write(tag, "Device model: " + Build.MODEL);
        write(tag, "Android OS: " + Build.VERSION.RELEASE);
        write(tag, "------------------------------------");
    }

    /**
     * Запись в протокол события
     *
     * @param tag  имя класса-инициатора события
     * @param text текст помещаемый в протокол событий
     */
    private void write(String tag, String text) {
        if (logFile == null) {
            return;
        }

        try (
                OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(logFile, true), Charset.forName("UTF-8"))
        ) {
            writer.write(String.format("%s %s %s%n",
                    new SimpleDateFormat(DATE_PATTERN, Locale.US).format(new Date()), tag, text));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
