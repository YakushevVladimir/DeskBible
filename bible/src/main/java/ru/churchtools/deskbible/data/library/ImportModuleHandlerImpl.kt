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
 * File: LoadModuleHandlerImpl.kt
 *
 * Created by Vladimir Yakushev at 12/2022
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.data.library

import android.content.Context
import android.net.Uri
import com.BibleQuote.domain.controller.ILibraryController
import com.BibleQuote.utils.FilenameUtils
import ru.churchtools.deskbible.di.app.ApplicationContext
import ru.churchtools.deskbible.domain.library.ImportModuleHandler
import ru.churchtools.deskbible.domain.library.ImportModuleHandler.StatusCode
import ru.churchtools.deskbible.domain.logger.StaticLogger
import java.io.File
import javax.inject.Inject

class ImportModuleHandlerImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val libraryController: ILibraryController,
    private val libraryContext: LibraryContext
) : ImportModuleHandler {

    override suspend fun loadModule(uri: Uri): StatusCode {
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri)
        if ("application/zip" != type) {
            return StatusCode.FileNotSupported
        }

        val fileName = FilenameUtils.getFileName(context, uri)
            ?: return StatusCode.FileNotExist

        val modulesDir = libraryContext.modulesDir()
        if (!modulesDir.exists() && !modulesDir.mkdirs()) {
            StaticLogger.error(this, "Library directory not found")
            return StatusCode.LibraryNotFound
        }

        try {
            contentResolver.openInputStream(uri)?.use { stream ->
                val target = File(modulesDir, fileName)
                stream.copyTo(target.outputStream())
                libraryController.loadModule(target)
            }
        } catch (e: Exception) {
            StaticLogger.error(this, e.message, e)
            return StatusCode.MoveFailed
        }

        return StatusCode.Success
    }
}