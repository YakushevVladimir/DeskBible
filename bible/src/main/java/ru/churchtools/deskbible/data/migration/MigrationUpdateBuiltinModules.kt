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
 * File: MigrationUpdateBuiltinModules.kt
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package ru.churchtools.deskbible.data.migration

import android.content.Context
import com.BibleQuote.R
import com.BibleQuote.domain.logger.StaticLogger
import com.BibleQuote.utils.DataConstants
import ru.churchtools.deskbible.data.library.LibraryContext
import ru.churchtools.deskbible.domain.migration.Migration
import java.io.File
import java.io.IOException

/**
 * Класс для обновления встроенных модулей приложения
 *
 * Копирует модули из папки ресурсов приложения в папку для модулей
 *
 * @author Vladimir Yakushev <ru.phoenix@gmail.com>
 */
class MigrationUpdateBuiltinModules(
    private val libraryContext: LibraryContext,
    private val context: Context,
    versionCode: Int
) : Migration(versionCode) {

    private val modules = mapOf(
        R.raw.bible_rst to RST_FILE_NAME,
        R.raw.bible_ubio to UBIO_FILE_NAME,
        R.raw.bible_kjv to KJV_FILE_NAME
    )

    override fun doMigrate() {
        StaticLogger.info(this, "Update built-in modules into ${libraryContext.libraryDir()}")

        // Удаление ранее скопированных встроенных модулей и файлов библиотеки
        libraryContext.libraryDir().deleteRecursively()
        DataConstants.getLibraryPath(context).deleteRecursively()
        File(context.filesDir, LibraryContext.FILE_CACHE).delete()

        val modulesDir = libraryContext.modulesDir()
        if (!modulesDir.exists() && !modulesDir.mkdirs()) {
            throw IOException("Modules folder create failed")
        }

        modules.entries.forEach { (resId, moduleFile) ->
            File(modulesDir, moduleFile).outputStream().use {
                context.resources.openRawResource(resId).copyTo(it)
            }
        }
    }

    override fun getMigrationDescription(): Int {
        return R.string.update_builtin_modules
    }

    private companion object {
        private const val RST_FILE_NAME = "bible_rst.zip"
        private const val UBIO_FILE_NAME = "bible_ubio.zip"
        private const val KJV_FILE_NAME = "bible_kjv.zip"
    }
}