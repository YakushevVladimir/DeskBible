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
 * File: CompositeLoggerTest.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.logger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class CompositeLoggerTest {

    private static final String TAG = CompositeLoggerTest.class.getSimpleName();
    private static final String MESSAGE = "test message";

    @Mock Logger logger1;
    @Mock Logger logger2;

    private CompositeLogger logger;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        logger = new CompositeLogger(Arrays.asList(logger1, logger2));
    }

    @Test
    public void debug() throws Exception {
        logger.debug(TAG, MESSAGE);
        verify(logger1).debug(eq(TAG), eq(MESSAGE));
        verify(logger2).debug(eq(TAG), eq(MESSAGE));
    }

    @Test
    public void error() throws Exception {
        Throwable th = new IndexOutOfBoundsException();
        logger.error(TAG, MESSAGE, th);
        verify(logger1).error(eq(TAG), eq(MESSAGE), eq(th));
        verify(logger2).error(eq(TAG), eq(MESSAGE), eq(th));
    }

    @Test
    public void errorWithThrowable() throws Exception {
        logger.error(TAG, MESSAGE);
        verify(logger1).error(eq(TAG), eq(MESSAGE));
        verify(logger2).error(eq(TAG), eq(MESSAGE));
    }

    @Test
    public void info() throws Exception {
        logger.info(TAG, MESSAGE);
        verify(logger1).info(eq(TAG), eq(MESSAGE));
        verify(logger2).info(eq(TAG), eq(MESSAGE));
    }
}