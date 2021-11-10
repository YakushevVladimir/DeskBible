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
 * File: ModuleTextFormatterTest.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.textFormatters;

import static org.mockito.Mockito.when;

import com.BibleQuote.domain.entity.BaseModule;
import com.BibleQuote.entity.modules.BQModule;
import com.BibleQuote.utils.PreferenceHelper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ModuleTextFormatterTest {

    @Mock PreferenceHelper prefHelper;

    private final String testVerses =
            "<p>12 Услышав же Иисус, что Иоанн отдан <I>под</I> <I>стражу,</I> удалился в Галилею\n" +
            "<p>13 и, оставив Назарет, пришел и поселился в Капернауме приморском, в пределах Завулоновых и Неффалимовых,";

    private final String testVersesWithStrong =
            "<p>12 Услышав же Иисус G1234 G59, что Иоанн отдан <I>под</I> <I>стражу,</I> удалился в Галилею\n" +
            "<p>13 и, оставив Назарет, пришел 1234 59 и поселился в Капернауме приморском, в пределах Завулоновых и Неффалимовых,";

    private BaseModule mModule;

    @Before
    public void testBefore() {
        MockitoAnnotations.initMocks(this);
        when(prefHelper.viewBookVerse()).thenReturn(true);

        mModule = new BQModule("base", "biblequote.ini");
        mModule.setContainsStrong(false);
        mModule.setBible(true);
    }

    @After
    public void testAfter() {
        mModule = null;
    }

    @Test
    public void testFullTagsClean() {
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule, new StripTagsTextFormatter());
        formatter.setVisibleVerseNumbers(false);

        String result = formatter.format(testVerses);
        Assert.assertFalse(result.contains("<I>"));
        Assert.assertFalse(result.contains("</I>"));
        Assert.assertFalse(result.contains("<p>"));
        Assert.assertFalse(result.contains("</p>"));
    }

    @Test
    public void testSetVisibleVerseNumbers() {
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule, prefHelper);
        formatter.setVisibleVerseNumbers(false);

        String result = formatter.format(testVerses);
        Assert.assertFalse(result.contains("12"));
        Assert.assertFalse(result.contains("13"));
    }

    @Test
    public void testFormatCleanTags() {
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule, prefHelper);

        String result = formatter.format(testVerses);
        Assert.assertTrue(result.contains("<I>"));
        Assert.assertTrue(result.contains("<p>"));
    }

    @Test
    public void testFormatModuleWithStrong() {
        mModule.setContainsStrong(true);
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule, prefHelper);

        String result = formatter.format(testVersesWithStrong);
        Assert.assertFalse(result.contains("G1234"));
        Assert.assertFalse(result.contains("G59"));
        Assert.assertFalse(result.contains("1234"));
        Assert.assertFalse(result.contains("59"));

        Pattern pattern = Pattern.compile("\\s{2,}");
        Matcher matcher = pattern.matcher(result);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void testFormatModuleWithoutStrong() {
        mModule.setContainsStrong(false);
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule, prefHelper);

        String result = formatter.format(testVersesWithStrong);
        Assert.assertTrue(result.contains("G1234"));
        Assert.assertTrue(result.contains("G59"));
        Assert.assertTrue(result.contains("1234"));
        Assert.assertTrue(result.contains("59"));
    }
}