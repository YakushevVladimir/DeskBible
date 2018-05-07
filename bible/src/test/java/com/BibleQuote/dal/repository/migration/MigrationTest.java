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
 * File: MigrationTest.java
 *
 * Created by Vladimir Yakushev at 5/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository.migration;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MigrationTest {

    @Test
    public void compareTo() throws Exception {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new MigrationStub(2, 3));
        migrations.add(new MigrationStub(1, 2));
        migrations.add(new MigrationStub(1, 3));
        migrations.add(new MigrationStub(3, 4));

        Collections.sort(migrations);
        assertThat(migrations.get(0), equalTo(new MigrationStub(1, 3)));
        assertThat(migrations.get(1), equalTo(new MigrationStub(1, 2)));
        assertThat(migrations.get(2), equalTo(new MigrationStub(2, 3)));
        assertThat(migrations.get(3), equalTo(new MigrationStub(3, 4)));
    }

    @Test
    public void equalsTest() throws Exception {
        assertEquals(new MigrationStub(1, 2), new MigrationStub(1, 2));
        assertNotEquals(new MigrationStub(1, 2), new MigrationStub(1, 3));
        assertNotEquals(new MigrationStub(1, 3), new MigrationStub(2, 3));
        assertNotEquals(new MigrationStub(1, 2), new MigrationStub(2, 3));
    }

    @Test
    public void hashCodeTest() throws Exception {
        MigrationStub migrationStub1 = new MigrationStub(1, 2);
        MigrationStub migrationStub2 = new MigrationStub(1, 2);

        // один и тот же объект всегда дает одинаковый хэш-код
        assertEquals(migrationStub1.hashCode(), migrationStub1.hashCode());

        // если два объекта равны, то и хэш-коды равны
        assertEquals(migrationStub1, migrationStub2);
        assertEquals(migrationStub1.hashCode(), migrationStub2.hashCode());
    }

    private static class MigrationStub extends Migration {

        MigrationStub(int oldVersion, int newVersion) {
            super(oldVersion, newVersion);
        }

        @Override
        public void migrate(SQLiteDatabase database) {

        }
    }
}