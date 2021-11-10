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
 * File: MoveLibraryDatabaseFromSdcardMigration.kt
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.data.migration

import android.content.Context
import android.os.Environment
import com.BibleQuote.R
import com.BibleQuote.dal.DbLibraryHelper
import com.BibleQuote.domain.logger.StaticLogger
import com.BibleQuote.utils.DataConstants
import ru.churchtools.deskbible.domain.migration.Migration
import java.io.File
import java.io.IOException

class MoveLibraryDatabaseFromSdcardMigration(
    private val context: Context,
    version: Int
) : Migration(version) {

    override fun getMigrationDescription(): Int {
        return R.string.upgrade_database
    }

    override fun doMigrate() {
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
            // карта памяти недоступна, невозможно переместить БД
            return
        }

        val externalDbDir = File(DataConstants.getDbExternalDataPath())
        if (!externalDbDir.exists()) {
            return
        }

        try {
            externalDbDir.listFiles {
                    pathname -> pathname.name.contains(DbLibraryHelper.DB_NAME)
            }?.forEach { file ->
                file.copyTo(context.getDatabasePath(file.name))
            }
            StaticLogger.info(this, "Файл БД закладок перенесен на карту памяти")
        } catch (ex: IOException) {
            StaticLogger.error(this, "Перемещение БД закладок не удалось", ex)
        }
    }
}
