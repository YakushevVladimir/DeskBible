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
 * File: Logger.kt
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.domain.logger

interface Logger {
    /**
     * Запись в протокол событий отладочного сообщения
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    fun debug(tag: String, message: String)

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    fun error(tag: String, message: String)

    /**
     * Запись в протокол событий сообщения об ошибке
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     * @param th      ссылка на полученный Exception
     */
    fun error(tag: String, message: String, th: Throwable)

    /**
     * Запись в протокол событий информационного сообщения
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    fun info(tag: String, message: String)

    /**
     * Запись в протокол событий сообщения с предупреждением
     *
     * @param tag     имя класса-инициатора события
     * @param message текст помещаемый в протокол событий
     */
    fun warn(tag: String, message: String)
}