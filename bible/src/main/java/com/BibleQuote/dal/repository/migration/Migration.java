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
 * File: Migration.java
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.migration;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Базовый класс для выполнения миграции БД
 *
 * @author Yakushev V.V. ru.phoenix@gmail.com
 * @since 07.05.2018
 */
public abstract class Migration implements Comparable<Migration> {

    /**
     * Версия БД, с которой выполняется миграция
     */
    public final int oldVersion;

    /**
     * Версия БД, на которую выполняется миграция
     */
    public final int newVersion;

    public Migration(int oldVersion, int newVersion) {
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
    }

    @Override
    public int compareTo(@NonNull Migration o) {
        if (o.oldVersion < this.oldVersion) {
            return 1;
        } else if (o.oldVersion == this.oldVersion && o.newVersion > this.newVersion) {
            return 1;
        } else if (o.oldVersion == this.oldVersion && o.newVersion == this.newVersion) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Migration migration = (Migration) o;

        return oldVersion == migration.oldVersion
                && newVersion == migration.newVersion;
    }

    @Override
    public int hashCode() {
        int result = oldVersion;
        result = 31 * result + newVersion;
        return result;
    }

    /**
     * Запуск процедуры миграции.
     *
     * @param database ссылка на БД, для которой выполняется миграция
     */
    public abstract void migrate(SQLiteDatabase database);
}
