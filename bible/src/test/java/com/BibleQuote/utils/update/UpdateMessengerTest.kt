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
 * File: UpdateMessengerTest.kt
 *
 * Created by Vladimir Yakushev at 4/2018
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils.update

import io.reactivex.Emitter
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class UpdateMessengerTest {

    @Mock
    lateinit var emitter: Emitter<String>
    lateinit var messenger: UpdateMessenger

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        messenger = UpdateMessenger(emitter)
    }

    @Test
    fun sendMessage() {
        messenger.sendMessage(MESSAGE)
        verify(emitter).onNext(eq(MESSAGE))
    }

    @Test
    fun sendNullMessage() {
        messenger.sendMessage(null)
        verify(emitter, never()).onNext(any())
    }

    companion object {
        const val MESSAGE = "hello"
    }
}