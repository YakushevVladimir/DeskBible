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
 * File: Migration.kt
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.update

import android.content.Context
import com.BibleQuote.domain.logger.StaticLogger

/**
 * Описывает интерфейс классов миграции приложения.
 *
 * @param migrationVersion версия приложения, начиная с которой должна выполняться миграция
 */
abstract class Migration(
        val migrationVersion: Int
) {

    /**
     * Метод выполнения миграции.
     *
     * Сравнивает [текущую версию приложения][versionCode] и [версию миграции,
     * указанную для текущего объекта][migrationVersion], и, если текущая версия приложения меньше,
     * запускает миграцию.
     *
     * @param context контекст приложения
     * @param versionCode текущая версия приложения
     * @param messenger мессенжер для отправки сообщений о процессе обновления
     */
    fun migrate(context: Context, versionCode: Int, messenger: UpdateMessenger) {
        if (versionCode < migrationVersion) {
            sendMessage(messenger, getInfoMessage(context))
            doMigrate(context)
        }
    }

    /**
     * Выполнение процедуры обновления
     *
     * @param context контекст приложения
     */
    protected abstract fun doMigrate(context: Context)

    /**
     * Получение описания выполняемого обновления
     *
     * @param context контекст приложения
     * @return строка с описания выполняемого обновления
     */
    protected abstract fun getInfoMessage(context: Context): String

    private fun sendMessage(messenger: UpdateMessenger, message: String) {
        StaticLogger.info(this, message)
        messenger.sendMessage(message)
    }
}
