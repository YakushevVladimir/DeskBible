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
 * File: CompositeLoggerTest.java
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */
package ru.churchtools.deskbible.domain.logger

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Тесты для [CompositeLogger]
 */
class CompositeLoggerTest {

    private val logger1: Logger = mockk(relaxed = true)
    private val logger2: Logger = mockk(relaxed = true)

    private lateinit var logger: CompositeLogger

    @Before
    fun setUp() {
        logger = CompositeLogger(listOf(logger1, logger2))
    }

    @Test
    fun debug() {
        logger.debug(TAG, MESSAGE)
        verify {
            logger1.debug(TAG, MESSAGE)
            logger2.debug(TAG, MESSAGE)
        }
    }

    @Test
    fun error() {
        val th: Throwable = IndexOutOfBoundsException()
        logger.error(TAG, MESSAGE, th)
        verify {
            logger1.error(TAG, MESSAGE, th)
            logger2.error(TAG, MESSAGE, th)
        }
    }

    @Test
    fun errorWithThrowable() {
        logger.error(TAG, MESSAGE)
        verify {
            logger1.error(TAG, MESSAGE)
            logger2.error(TAG, MESSAGE)
        }
    }

    @Test
    fun info() {
        logger.info(TAG, MESSAGE)
        verify {
            logger1.info(TAG, MESSAGE)
            logger2.info(TAG, MESSAGE)
        }
    }

    companion object {
        private const val TAG = "CompositeLoggerTest"
        private const val MESSAGE = "test message"
    }
}