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
 * File: MigrationTSKSource.kt
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.data.migration

import android.content.Context
import com.BibleQuote.R
import com.BibleQuote.domain.logger.StaticLogger
import ru.churchtools.deskbible.data.library.LibraryContext
import ru.churchtools.deskbible.data.library.LibraryContext.Companion.FILE_TSK
import ru.churchtools.deskbible.domain.migration.Migration
import java.io.File

class TSKSourceMigration(
    private val libraryContext: LibraryContext,
    private val context: Context,
    version: Int
) : Migration(version) {

    override fun doMigrate() {
        removeOldTSKFile()
        saveTSK()
    }

    override fun getMigrationDescription(): Int {
        return R.string.update_tsk
    }

    private fun removeOldTSKFile() {
        val oldFile = File(context.filesDir, FILE_TSK)
        if (oldFile.exists() && oldFile.delete()) {
            StaticLogger.info(this, "Old TSK file removed")
        }
    }

    private fun saveTSK() {
        StaticLogger.info(this, "Save TSK file")
        context.resources.openRawResource(R.raw.tsk).use { stream ->
            stream.copyTo(libraryContext.tskFile().outputStream())
        }
    }
}