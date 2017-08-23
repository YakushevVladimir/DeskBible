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
 * File: BacklightTextFormatter.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.textFormatters;

import java.util.regex.Pattern;

public class BacklightTextFormatter implements ITextFormatter {

    private final ITextFormatter formatter;
    private final String query;
    private final String colorPattern;

    public BacklightTextFormatter(ITextFormatter baseFormatter, String query, String color) {
        this.query = query;
        this.formatter = baseFormatter;
        this.colorPattern = "<b><font color=\"" + color + "\">$1</font></b>";
    }

    @Override
    public String format(String text) {
        String[] words = query.toLowerCase().replaceAll("[^\\s\\w]", "").split("\\s+");
        StringBuilder pattern = new StringBuilder(query.length() + words.length);
        for (String word : words) {
            if (pattern.length() != 0) {
                pattern.append("|");
            }
            pattern.append(word);
        }

        Pattern regex = Pattern.compile("((?ui)" + pattern.toString() + ")");
        return regex.matcher(formatter.format(text)).replaceAll(colorPattern);
    }
}
