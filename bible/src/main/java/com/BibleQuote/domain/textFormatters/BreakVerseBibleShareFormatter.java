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
 * File: BreakVerseBibleShareFormatter.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.textFormatters;

import java.util.LinkedHashMap;
import java.util.Map;

public class BreakVerseBibleShareFormatter implements IShareTextFormatter {

    private LinkedHashMap<Integer, String> verses;

    public BreakVerseBibleShareFormatter(LinkedHashMap<Integer, String> verses) {
        this.verses = verses;
    }

    @Override
    public String format() {
        StringBuilder shareText = new StringBuilder();
        Integer prevVerseNumber = 0;
        for (Map.Entry<Integer, String> entry : verses.entrySet()) {
            if (prevVerseNumber == 0) {
                prevVerseNumber = entry.getKey();
            }

            if (entry.getKey() - prevVerseNumber > 1) {
                shareText.append("...\r\n");
            }
            shareText.append(String.format("%1$s %2$s", entry.getKey(), entry.getValue())).append("\r\n");
            prevVerseNumber = entry.getKey();
        }
        return shareText.toString();
    }

}
