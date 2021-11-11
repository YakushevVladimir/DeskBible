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
 * File: StaticLogger.java
 *  
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.logger;

public final class StaticLogger {
    
    private static Logger logger;

    private StaticLogger() {
    }
    
    /**
     * Запись в протокол событий отладочного сообщения
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    public static void debug(Object tag, String message) {
        if (logger != null) logger.debug(tag, message);
    }

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag  имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    public static void error(Object tag, String message) {
        if (logger != null) logger.error(tag, message);
    }

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag  имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     * @param th   ссылка на полученный Exception
     */
    public static void error(Object tag, String message, Throwable th) {
        if (logger != null) logger.error(tag, message, th);
    }

    /**
     * Запись в протокол событий информационного сообщения
     *
     * @param tag  имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    public static void info(Object tag, String message) {
        if (logger != null) logger.info(tag, message);
    }

    public static void init(Logger logger) {
        StaticLogger.logger = logger;
    }
}
