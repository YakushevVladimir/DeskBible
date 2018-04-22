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
 * File: MigrationTSKSource.kt
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.update.migration

import android.content.Context
import android.util.Xml
import com.BibleQuote.R
import com.BibleQuote.domain.logger.StaticLogger
import com.BibleQuote.utils.DataConstants
import com.BibleQuote.utils.update.Migration
import java.io.*
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class MigrationTSKSource(migrateVersion: Int) : Migration(migrateVersion) {

    private val tskFileName = "tsk.xml"

    override fun doMigrate(context: Context) {
        removeOldTSKFile()
        saveTSK(context)
    }

    override fun getInfoMessage(context: Context): String {
        return context.getString(R.string.update_tsk)
    }

    private fun removeOldTSKFile() {
        val oldFile = File(DataConstants.getFsAppDirName(), tskFileName)
        if (oldFile.exists() && oldFile.delete()) {
            StaticLogger.info(this, "Old TSK file removed")
        }
    }

    private fun saveTSK(context: Context) {
        StaticLogger.info(this, "Save TSK file")
        var tskBw: BufferedWriter? = null
        var tskBr: BufferedReader? = null
        var zStream: ZipInputStream? = null
        try {
            val tskStream = context.resources.openRawResource(R.raw.tsk)
            zStream = ZipInputStream(tskStream)

            var isReader: InputStreamReader? = null
            var entry: ZipEntry? = zStream.nextEntry
            while (entry != null) {
                var entryName = entry.name.toLowerCase()
                if (entryName.contains(File.separator)) {
                    entryName = entryName.substring(entryName.lastIndexOf(File.separator) + 1)
                }
                if (entryName.equals(tskFileName, ignoreCase = true)) {
                    isReader = InputStreamReader(zStream, Xml.Encoding.UTF_8.toString())
                    break
                }
                entry = zStream.nextEntry
            }
            if (isReader == null) {
                return
            }

            tskBr = BufferedReader(isReader)

            val tskFile = File(context.filesDir, tskFileName)
            if (tskFile.exists() && !tskFile.delete()) {
                StaticLogger.error(this, "Can't delete TSK-file")
                return
            }
            tskBw = BufferedWriter(OutputStreamWriter(FileOutputStream(tskFile), Charset.forName("UTF-8")))

            val buf = CharArray(1024)
            var len: Int = tskBr.read(buf)
            while (len > 0) {
                tskBw.write(buf, 0, len)
                len = tskBr.read(buf)
            }
            tskBw.flush()
        } catch (e: IOException) {
            StaticLogger.error(this, e.message)
        } finally {
            closeSafely(tskBr)
            closeSafely(tskBw)
            closeSafely(zStream)
        }
    }

    private fun closeSafely(tskBr: Closeable?) {
        try {
            tskBr?.close()
        } catch (e: IOException) {
            StaticLogger.error(this, e.message)
        }
    }
}