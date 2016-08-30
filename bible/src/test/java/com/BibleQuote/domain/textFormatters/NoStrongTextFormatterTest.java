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
 * File: NoStrongTextFormatterTest.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.textFormatters;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 01.2016
 */
public class NoStrongTextFormatterTest {

    @Test
    public void testFormat() throws Exception {
        NoStrongTextFormatter formatter = new NoStrongTextFormatter();

        String result = formatter.format(
                "<p><sup>1</sup> НектоH376 из племениH1004 ЛевиинаH3878 пошелH3212 и " +
                        "взялH3947 себе женуH1323 из того же племениH3878.");
        Assert.assertFalse(result.contains("H3947"));

        result = formatter.format(
                "<p><sup>1</sup> Некто H376 из племени H1004 Левиина H3878 пошел H3212 и " +
                        "взял H3947 себе жену H1323 из того же племени H3878.");
        Assert.assertFalse(result.contains("H3947"));
    }
}