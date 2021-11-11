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
 * File: CrashlyticsLogger.java
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */
package ru.churchtools.deskbible.data.logger

import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.churchtools.deskbible.domain.logger.Logger

/**
 * Логирование событий в Crashlytics
 *
 * @author Vladimir Yakushev <ru.phoenix@gmail.com>
 */
class CrashlyticsLogger(
    private val crashlytics: FirebaseCrashlytics
) : Logger {

    override fun debug(tag: String, message: String) {
        crashlytics.log(formatMessage("D", tag, message))
    }

    override fun error(tag: String, message: String) {
        crashlytics.log(formatMessage("E", tag, message))
    }

    override fun error(tag: String, message: String, th: Throwable) {
        crashlytics.log(formatMessage("E", tag, message))
        crashlytics.recordException(th)
    }

    override fun info(tag: String, message: String) {
        crashlytics.log(formatMessage("I", tag, message))
    }

    override fun warn(tag: String, message: String) {
        crashlytics.log(formatMessage("W", tag, message))
    }

    private fun formatMessage(logLevel: String, tag: String, message: String) = "$logLevel/$tag: $message"
}