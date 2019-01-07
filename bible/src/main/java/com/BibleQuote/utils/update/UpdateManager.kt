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
 * File: UpdateManager.kt
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.update

import android.content.Context
import com.BibleQuote.BuildConfig
import com.BibleQuote.dal.DbLibraryHelper
import com.BibleQuote.domain.controller.ILibraryController
import com.BibleQuote.domain.logger.StaticLogger
import com.BibleQuote.utils.PreferenceHelper
import com.BibleQuote.utils.update.migration.*
import io.reactivex.Observable
import javax.inject.Inject

class UpdateManager @Inject constructor(
        context: Context,
        private val prefHelper: PreferenceHelper,
        libraryController: ILibraryController,
        dbLibraryHelper: DbLibraryHelper
) {

    private val appContext: Context = context.applicationContext
    private val migrationList = listOf(
            MigrationPreferenceColor(84, prefHelper),
            MigrationTSKSource(85),
            MigrationUpdateBuiltinModules(85),
            MigrationReloadModules(85, libraryController),
            Migration86(dbLibraryHelper)
    )

    fun update(): Observable<String> {
        return Observable.create { emitter ->

            StaticLogger.info(this, "Start update manager...")

            val currVersionCode = prefHelper.getInt("versionCode")
            val messenger = UpdateMessenger(emitter)
            migrationList.forEach { it.migrate(appContext, currVersionCode, messenger) }

            prefHelper.saveInt("versionCode", BuildConfig.VERSION_CODE)

            StaticLogger.info(this, "Update success")
            emitter.onComplete()
        }
    }
}
