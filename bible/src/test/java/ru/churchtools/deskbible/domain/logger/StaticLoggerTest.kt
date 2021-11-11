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
 * File: StaticLoggerTest.java
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

class StaticLoggerTest {

    private val logger: Logger = mockk(relaxed = true)
    private val objectTag = "${this.javaClass.simpleName}(${this.hashCode()})"

    @Before
    fun setUp() {
        StaticLogger.init(logger)
    }

    @Test
    fun debug() {
        StaticLogger.debug(TAG, MESSAGE)
        verify { logger.debug(TAG, MESSAGE) }
    }

    @Test
    fun debugWithObjectTag() {
        StaticLogger.debug(this, MESSAGE)
        verify { logger.debug(objectTag, MESSAGE) }
    }

    @Test
    fun error() {
        StaticLogger.error(TAG, MESSAGE)
        verify { logger.error(TAG, MESSAGE) }
    }

    @Test
    fun errorWithObjectTag() {
        StaticLogger.error(this, MESSAGE)
        verify { logger.error(objectTag, MESSAGE) }
    }

    @Test
    fun errorWithThrowable() {
        val th: Throwable = IndexOutOfBoundsException()
        StaticLogger.error(TAG, MESSAGE, th)
        verify { logger.error(TAG, MESSAGE, th) }
    }

    @Test
    fun errorWithThrowableAndObjectTag() {
        val th: Throwable = IndexOutOfBoundsException()
        StaticLogger.error(this, MESSAGE, th)
        verify { logger.error(objectTag, MESSAGE, th) }
    }

    @Test
    fun info() {
        StaticLogger.info(TAG, MESSAGE)
        verify { logger.info(TAG, MESSAGE) }
    }

    @Test
    fun infoWithObjectTag() {
        StaticLogger.info(this, MESSAGE)
        verify { logger.info(objectTag, MESSAGE) }
    }

    @Test
    fun warn() {
        StaticLogger.warn(TAG, MESSAGE)
        verify { logger.warn(TAG, MESSAGE) }
    }

    @Test
    fun warnWithObjectTag() {
        StaticLogger.warn(this, MESSAGE)
        verify { logger.warn(objectTag, MESSAGE) }
    }

    @Test
    fun nonInit() {
        StaticLogger.init(null)
        StaticLogger.info(TAG, MESSAGE)
    }

    @Test
    fun nonInitWithObjectTag() {
        StaticLogger.init(null)
        StaticLogger.info(this, MESSAGE)
    }

    companion object {
        private const val TAG = "StaticLoggerTest"
        private const val MESSAGE = "test message"
    }
}