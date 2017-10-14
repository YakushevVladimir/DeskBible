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
 * File: StaticLoggerTest.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.logger;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StaticLoggerTest {

    private static final String TAG = StaticLoggerTest.class.getSimpleName();
    private static final String MESSAGE = "test message";
    private static final Logger logger = mock(Logger.class);

    @Before
    public void setUp() throws Exception {
        StaticLogger.init(logger);
    }

    @Test
    public void debug() throws Exception {
        StaticLogger.debug(TAG, MESSAGE);
        verify(logger).debug(eq(TAG), eq(MESSAGE));
    }

    @Test
    public void error() throws Exception {
        StaticLogger.error(TAG, MESSAGE);
        verify(logger).error(eq(TAG), eq(MESSAGE));
    }

    @Test
    public void errorWithThrowable() throws Exception {
        Throwable th = new IndexOutOfBoundsException();
        StaticLogger.error(TAG, MESSAGE, th);
        verify(logger).error(eq(TAG), eq(MESSAGE), eq(th));
    }

    @Test
    public void info() throws Exception {
        StaticLogger.info(TAG, MESSAGE);
        verify(logger).info(eq(TAG), eq(MESSAGE));
    }

    @Test
    public void nonInit() throws Exception {
        StaticLogger.init(null);
        StaticLogger.info(TAG, MESSAGE);
    }
}