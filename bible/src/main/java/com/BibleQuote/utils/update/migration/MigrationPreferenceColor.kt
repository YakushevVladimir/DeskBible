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
 * File: MigrationPreferenceColor.kt
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.update.migration

import android.content.Context
import com.BibleQuote.R
import com.BibleQuote.domain.logger.StaticLogger
import com.BibleQuote.utils.ColorUtils
import com.BibleQuote.utils.PreferenceHelper
import com.BibleQuote.utils.update.Migration

class MigrationPreferenceColor(
        version: Int,
        private val preference: PreferenceHelper
) : Migration(version) {

    override fun doMigrate(context: Context) {
        try {
            preference.putInt("text_color", ColorUtils.toInt(preference.getString("TextColor", "#51150F")))
            preference.putInt("sel_text_color", ColorUtils.toInt(preference.getString("TextColorSel", "#51150F")))
            preference.putInt("background", ColorUtils.toInt(preference.getString("TextBG", "#faedc1")))
            preference.putInt("sel_background", ColorUtils.toInt(preference.getString("TextBGSel", "#f9d979")))
        } catch (ex: Exception) {
            StaticLogger.error(this, "Update preference color failed", ex)
        }
    }

    override fun getInfoMessage(context: Context): String {
        return context.getString(R.string.update_preferences)
    }
}