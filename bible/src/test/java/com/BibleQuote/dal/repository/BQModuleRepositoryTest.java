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
 * File: BQModuleRepositoryTest.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.dal.repository;

import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.FsUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BQModuleRepositoryTest {

    @Mock FsUtils fsUtils;
    @Mock BQModule module;
    private BQModuleRepository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        repository = new BQModuleRepository();
    }

    @Test
    public void convertImagePathsToBase64withNonArchiveModules() throws Exception {
        when(module.isArchive()).thenReturn(true);
        String testLine = "test string with <img src=\"bible.png\" alt=\"bible\"> and other symbols";
        String result = repository.cacheFileFromArchive(module, testLine);
        assertNotNull(result);
        assertEquals(testLine, result);
    }

    @Test
    public void convertImagePathsToBase64WithAltInEnd() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream("bible.png");
        when(FsUtils.getStreamFromZip(anyString(), anyString())).thenReturn(imageStream);
        when(module.isArchive()).thenReturn(true);
        when(module.getModulePath()).thenReturn("");

        String result1 = repository.cacheFileFromArchive(module, "test string with <img src=\"bible.png\" alt=\"bible\"> and other symbols");
        assertNotNull(result1);
        assertEquals(27921, result1.length());
    }

    @Test
    public void convertImagePathsToBase64WithAltInStart() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream("bible.png");
        when(FsUtils.getStreamFromZip(anyString(), anyString())).thenReturn(imageStream);
        when(module.isArchive()).thenReturn(true);
        when(module.getModulePath()).thenReturn("");

        String result2 = repository.cacheFileFromArchive(module, "test string with <img alt=\"bible\" src=\"bible.png\"> and other symbols");
        assertNotNull(result2);
        assertEquals(27921, result2.length());
    }

    @Test
    public void convertImagePathsToBase64WithoutAlt() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream("bible.png");
        when(FsUtils.getStreamFromZip(anyString(), anyString())).thenReturn(imageStream);
        when(module.isArchive()).thenReturn(true);
        when(module.getModulePath()).thenReturn("");

        String result3 = repository.cacheFileFromArchive(module, "test string with <img src='bible.png'> and other symbols");
        assertNotNull(result3);
        assertEquals(27909, result3.length());
    }

    @Test
    public void convertImagePathsToBase64WithoutAltLowerCase() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream("bible.png");
        when(FsUtils.getStreamFromZip(anyString(), anyString())).thenReturn(imageStream);
        when(module.isArchive()).thenReturn(true);
        when(module.getModulePath()).thenReturn("");

        String result3 = repository.cacheFileFromArchive(module, "<p><IMG SRC= \"theatre_seats.jpg\"> ".toLowerCase());
        assertNotNull(result3);
        assertEquals(27879, result3.length());
    }
}