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
 * File: DefaultLibraryContext.kt
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.data.library

import com.BibleQuote.utils.DataConstants
import ru.churchtools.deskbible.data.library.LibraryContext.Companion.DIR_LIBRARY
import ru.churchtools.deskbible.data.library.LibraryContext.Companion.DIR_MODULES
import ru.churchtools.deskbible.data.library.LibraryContext.Companion.FILE_CACHE
import ru.churchtools.deskbible.data.library.LibraryContext.Companion.FILE_TSK
import java.io.File

/**
 * Основная реализация контекста библиотеки приложения
 */
class DefaultLibraryContext(
    private val filesDir: File
) : LibraryContext {

    override fun libraryDir(): File = File(filesDir, DIR_LIBRARY)

    override fun libraryCacheFile(): File  = File(libraryDir(), FILE_CACHE)

    override fun modulesDir(): File = File(libraryDir(), DIR_MODULES)

    override fun modulesExternalDir(): File = File(DataConstants.getFsAppDirName(), DIR_MODULES)

    override fun tskFile(): File = File(libraryDir(), FILE_TSK)
}