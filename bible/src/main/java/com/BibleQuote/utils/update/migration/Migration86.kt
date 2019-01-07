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
 * File: Migration86.kt
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.update.migration

import android.content.Context
import android.os.Environment
import com.BibleQuote.R
import com.BibleQuote.dal.DbLibraryHelper
import com.BibleQuote.dal.repository.migration.Migration_2_3
import com.BibleQuote.domain.entity.Bookmark
import com.BibleQuote.domain.logger.StaticLogger
import com.BibleQuote.utils.DataConstants
import com.BibleQuote.utils.FsUtils
import com.BibleQuote.utils.update.Migration
import java.io.File
import java.io.IOException

class Migration86(private val dbLibraryHelper: DbLibraryHelper) : Migration(86) {

    override fun doMigrate(context: Context) {
        moveDatabaseToSdCard(context)
        repairDatabase()
        updateBookmarksTime()
    }

    override fun getInfoMessage(context: Context): String {
        return context.getString(R.string.upgrade_database)
    }

    private fun moveDatabaseToSdCard(context: Context) {
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
            // карта памяти недоступна, невозможно переместить БД
            return
        }

        val dbDataPathFile = File(DataConstants.getDbDataPath())
        val filesDir = context.filesDir
        val dbFile = FsUtils.findFile(DbLibraryHelper.DB_NAME, dbDataPathFile, filesDir)
        if (dbFile == null) {
            StaticLogger.info(this, "Файл БД в приватных папках приложения не найден")
            return
        }

        val destDir = File(DataConstants.getDbExternalDataPath())
        if (!destDir.exists() && !destDir.mkdirs()) {
            StaticLogger.error(this, "Не удалось создать папку для перемещения БД")
            return
        }

        try {
            val dbDir = dbFile.parentFile

            dbFile.copyTo(File(destDir, DbLibraryHelper.DB_NAME), true)
            dbFile.delete()

            val dbJournalFile = "${DbLibraryHelper.DB_NAME}-journal"
            File(dbDir, dbJournalFile).let {
                it.copyTo(File(destDir, dbJournalFile))
                it.delete()
            }
            StaticLogger.info(this, "Файл БД закладок перенесен на карту памяти")
        } catch (ex: IOException) {
            StaticLogger.error(this, "Перемещение БД закладок не удалось", ex)
        }
    }

    private fun repairDatabase() {
        val database = dbLibraryHelper.database
        var dbActualVersion = database.version
        if (dbActualVersion != 3) {
            StaticLogger.error(this, String.format(
                    "Некорректная версия БД (%d) для миграции приложения на версию %d",
                    dbActualVersion, migrationVersion))
            return
        }

        // Проверяем обновлена ли БД на версию 2 и 3.
        // Проверку осуществляем по наличию в таблице закладок колонок time и name

        val query = String.format("PRAGMA table_info(%s);", DbLibraryHelper.BOOKMARKS_TABLE)
        database.rawQuery(query, arrayOf()).use { cursor ->
            if (!cursor.moveToFirst()) {
                StaticLogger.error(this, "Не удалось получить информацию о таблице с закладками")
                return
            }

            val nameIndex = cursor.getColumnIndex("name")
            if (nameIndex == -1) {
                StaticLogger.error(this, "Не удалось получить информацию о колонке `name` в таблице с закладками")
                return
            }

            var hasTimeColumn = false
            var hasNameColumn = false
            do {
                val columnName = cursor.getString(nameIndex)
                if (columnName == Bookmark.TIME) {
                    hasTimeColumn = true
                } else if (columnName == Bookmark.NAME) {
                    hasNameColumn = true
                }
            } while (cursor.moveToNext())

            if (!hasNameColumn) {
                dbActualVersion = 1
            } else if (!hasTimeColumn) {
                dbActualVersion = 2
            } else {
                StaticLogger.info(this, "БД находится в актуальном состоянии")
                return
            }
        }

        StaticLogger.info(this, String.format("Версия БД снижена до %d версии", dbActualVersion))
        database.version = dbActualVersion
        dbLibraryHelper.closeDatabase()
    }

    private fun updateBookmarksTime() {
        val database = dbLibraryHelper.database
        Migration_2_3.setBookmarksTime(database)
    }
}
