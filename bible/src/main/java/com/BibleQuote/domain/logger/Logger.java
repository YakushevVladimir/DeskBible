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
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.logger;

import androidx.annotation.NonNull;

public abstract class Logger {

    /**
     * Запись в протокол событий отладочного сообщения
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    public abstract void debug(@NonNull Object tag, @NonNull String message);

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    public abstract void error(@NonNull Object tag, @NonNull String message);

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     * @param th      ссылка на полученный Exception
     */
    public abstract void error(@NonNull Object tag, @NonNull String message, @NonNull Throwable th);

    /**
     * Запись в протокол событий информационного сообщения
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    public abstract void info(@NonNull Object tag, @NonNull String message);

    protected String getTag(@NonNull Object src) {
        if (src instanceof String) {
            return (String) src;
        } else {
            return String.format("%s (%d)", src.getClass().getSimpleName(), src.hashCode());
        }
    }
}
