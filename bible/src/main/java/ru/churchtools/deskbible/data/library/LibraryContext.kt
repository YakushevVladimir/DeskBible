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
 * File: LibraryContext.kt
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.data.library

import java.io.File

/**
 * Контекст библиотеки приложения, хранящий данные о размещении различных модулей
 * для работы с Библией
 *
 * @author Yakushev Vladimir <ru.phoenix@gmail.com>
 */
interface LibraryContext {

    /**
     * Корневая директория с файлами для работы с библиотекой
     */
    fun libraryDir(): File

    /**
     * Файл со списком модулей
     */
    fun libraryCacheFile(): File

    /**
     * Директория с файлами модулей (лежит внутри корневой директории библиотеки)
     */
    fun modulesDir(): File

    /**
     * Директория с файлами модулей на внешнем носителе
     */
    fun modulesExternalDir(): File

    /**
     * Файл с параллельными местами Писания
     */
    fun tskFile(): File

    companion object {
        const val DIR_LIBRARY = "library"
        const val DIR_MODULES = "modules"
        const val FILE_CACHE = "library.cache"
        const val FILE_TSK = "tsk.xml"
    }
}