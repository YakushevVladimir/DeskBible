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
package com.BibleQuote.data.logger.file;

import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import androidx.annotation.NonNull;
import android.util.Log;

import com.BibleQuote.BuildConfig;
import com.BibleQuote.domain.logger.Logger;

/**
 * Класс отвечающий за запись протокола событий приложения.
 * Записывает отладочную информация в файл log.txt,
 * находящийся в корне съемного диска устройства
 *
 * @author Vladimir Yakushev <ru.phoenix@gmail.com>
 */
public final class FileLogger extends Logger {

    private static final String TAG = FileLogger.class.getSimpleName();

    @NonNull
    private final LoggerHandler mLoggerHandler;
    private final HandlerThread mHandlerThread;

    public FileLogger() {
        mHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_LOWEST);
        mHandlerThread.start();
        mLoggerHandler = new LoggerHandler(mHandlerThread.getLooper(), TAG);
    }

    @Override
    public void debug(@NonNull Object tag, @NonNull String message) {
        if (BuildConfig.DEBUG) {
            write(getTag(tag), message);
        }
    }

    @Override
    public void error(@NonNull Object tag, @NonNull String message) {
        write(getTag(tag), "Error: " + message);
    }

    @Override
    public void error(@NonNull Object tag, @NonNull String message, @NonNull Throwable th) {
        write(getTag(tag), String.format("Error: %s%n%s", message, Log.getStackTraceString(th)));
    }

    @Override
    public void info(@NonNull Object tag, @NonNull String message) {
        write(getTag(tag), message);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mHandlerThread.quitSafely();
    }

    /**
     * Запись в протокол события
     *
     * @param tag  имя класса-инициатора события
     * @param text текст помещаемый в протокол событий
     */
    private void write(String tag, String text) {
        mLoggerHandler.sendMessage(Message.obtain(mLoggerHandler, LoggerHandler.ACTION_WRITE, new LogMessage(tag, text)));
    }
}
