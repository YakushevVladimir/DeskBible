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
 * File: LoggerTest.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.logger;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class LoggerTest {

    private final TestLogger logger = new TestLogger();

    @Test
    public void getTag() {
        Date test = new Date();
        assertEquals(String.format("%s (%d)", test.getClass().getSimpleName(), test.hashCode()), logger.getTag(test));
        assertEquals("test", logger.getTag("test"));
    }

    private static class TestLogger extends Logger {

        @Override
        public void debug(@NonNull Object tag, @NonNull String message) {

        }

        @Override
        public void error(@NonNull Object tag, @NonNull String message) {

        }

        @Override
        public void error(@NonNull Object tag, @NonNull String message, @NonNull Throwable th) {

        }

        @Override
        public void info(@NonNull Object tag, @NonNull String message) {

        }
    }
}