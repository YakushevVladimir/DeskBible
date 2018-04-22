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

package com.BibleQuote.utils.update.migration

import android.content.Context
import com.BibleQuote.BuildConfig
import com.BibleQuote.R
import com.BibleQuote.domain.logger.StaticLogger
import com.BibleQuote.utils.update.Migration
import java.io.File
import java.io.FileOutputStream

class MigrationUpdateBuiltinModules(versionCode: Int) : Migration(versionCode) {

    override fun doMigrate(context: Context) {
        val dir = File(context.filesDir, BuildConfig.MODULE_DIR_NAME)
        if (!dir.exists() && !dir.mkdir()) {
            StaticLogger.error(this, "Create module dir failure")
            return
        }

        mapOf(R.raw.bible_rst to "bible_rst.zip", R.raw.bible_kjv to "bible_kjv.zip")
                .entries
                .forEach { (resId, moduleFile) ->
                    context.resources.openRawResource(resId).copyTo(FileOutputStream(File(dir, moduleFile)))
                }
    }

    override fun getInfoMessage(context: Context): String {
        return context.getString(R.string.update_builtin_modules)
    }
}