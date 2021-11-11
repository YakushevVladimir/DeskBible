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
 * Project: DeskBible
 * File: StaticLogger.kt
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */
package ru.churchtools.deskbible.domain.logger

object StaticLogger {

    private var LOGGER_INSTANCE: Logger? = null

    /**
     * Запись в протокол событий отладочного сообщения
     *
     * @param tag имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    @JvmStatic
    fun debug(tag: Any, message: String?) {
        LOGGER_INSTANCE?.debug(getTag(tag), message.orEmpty())
    }

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    @JvmStatic
    fun error(tag: Any, message: String?) {
        LOGGER_INSTANCE?.error(getTag(tag), message.orEmpty())
    }

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     * @param th ссылка на полученный Exception
     */
    @JvmStatic
    fun error(tag: Any, message: String?, th: Throwable) {
        LOGGER_INSTANCE?.error(getTag(tag), message.orEmpty(), th)
    }

    /**
     * Запись в протокол событий информационного сообщения
     *
     * @param tag имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    @JvmStatic
    fun info(tag: Any, message: String?) {
        LOGGER_INSTANCE?.info(getTag(tag), message.orEmpty())
    }

    /**
     * Запись в протокол событий сообщения с предупреждением
     *
     * @param tag имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    @JvmStatic
    fun warn(tag: Any, message: String?) {
        LOGGER_INSTANCE?.warn(getTag(tag), message.orEmpty())
    }

    @JvmStatic
    fun init(logger: Logger?) {
        LOGGER_INSTANCE = logger
    }

    private fun getTag(src: Any): String =
        if (src is String) {
            src
        } else {
            "${src.javaClass.simpleName}(${src.hashCode()})"
        }
}