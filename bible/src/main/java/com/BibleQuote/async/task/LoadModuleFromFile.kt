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
 * File: LoadModuleFromFile.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.async.task

import android.content.Context
import android.net.Uri
import com.BibleQuote.domain.controller.ILibraryController
import com.BibleQuote.utils.FilenameUtils
import com.BibleQuote.utils.Task
import ru.churchtools.deskbible.data.library.LibraryContext
import ru.churchtools.deskbible.domain.logger.StaticLogger.error
import ru.churchtools.deskbible.domain.logger.StaticLogger.info
import java.io.File
import java.lang.ref.WeakReference

/**
 * @author ru_phoenix
 * @version 1.0
 */
class LoadModuleFromFile(
    context: Context,
    message: String,
    private val uri: Uri,
    private val libraryController: ILibraryController,
    private val libraryContext: LibraryContext
) : Task(message, false) {
    private val weakContext: WeakReference<Context> = WeakReference(context.applicationContext)

    var statusCode = StatusCode.Success
        private set

    override fun doInBackground(vararg arg0: String): Boolean {
        info(this, "Load module from $uri")

        val modulesDir = libraryContext.modulesDir()
        if (!modulesDir.exists() && !modulesDir.mkdirs()) {
            statusCode = StatusCode.LibraryNotFound
            error(this, "Library directory not found")
            return false
        }

        val context = weakContext.get() ?: return false
        val contentResolver = context.contentResolver

        val type = contentResolver.getType(uri)
        if ("application/zip" != type) {
            statusCode = StatusCode.FileNotSupported
            return false
        }

        val fileName = FilenameUtils.getFileName(context, uri)
        if (fileName == null) {
            statusCode = StatusCode.FileNotExist
            return false
        }

        try {
            contentResolver.openInputStream(uri)?.use { stream ->
                val target = File(modulesDir, fileName)
                stream.copyTo(target.outputStream())
                libraryController.loadModule(target)
            }
        } catch (e: Exception) {
            error(this, e.message, e)
            statusCode = StatusCode.MoveFailed
            return false
        }

        return true
    }

    enum class StatusCode {
        Success, FileNotExist, FileNotSupported, MoveFailed, LibraryNotFound
    }
}