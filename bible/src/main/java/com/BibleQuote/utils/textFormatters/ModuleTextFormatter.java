/*
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
 * --------------------------------------------------
 *
 * Project: BibleQuote-for-Android
 * File: ModuleTextFormatter.java
 *
 * Created by Vladimir Yakushev at 8/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 *
 */

package com.BibleQuote.utils.textFormatters;

import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.utils.PreferenceHelper;

import java.util.ArrayList;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 11.2015
 */
public class ModuleTextFormatter implements ITextFormatter {

    private static final String VERSE_NUMBER_PATTERN = "(?m)^(<[^/]+?>)*?(\\d+)(</(.)+?>){0,1}?\\s+";

    private ArrayList<ITextFormatter> formatters = new ArrayList<ITextFormatter>();
    private boolean visibleVerseNumbers;

    public ModuleTextFormatter(Module module) {
        this.visibleVerseNumbers = module.isBible || PreferenceHelper.restoreStateBoolean(PreferenceHelper.KEY_VIEW_BOOK_VERSE);
        if (module.containsStrong) {
            formatters.add(new NoStrongTextFormatter());
        }
        formatters.add(new StripTagsTextFormatter("<(?!" + module.HtmlFilter + ")(.)*?>"));
    }

    public ModuleTextFormatter(Module module, ITextFormatter formatter) {
        this.visibleVerseNumbers = module.isBible;
        if (module.containsStrong) {
            formatters.add(new NoStrongTextFormatter());
        }
        formatters.add(formatter);
    }

    public void setVisibleVerseNumbers(boolean visible) {
        this.visibleVerseNumbers = visible;
    }

    @Override
    public String format(String text) {
        for (ITextFormatter formatter : formatters) {
            text = formatter.format(text);
        }
        if (visibleVerseNumbers) {
            text = text.replaceAll(VERSE_NUMBER_PATTERN, "$1<b>$2</b>$3 ").replaceAll("null", "");
        } else {
            text = text.replaceAll(VERSE_NUMBER_PATTERN, "");
        }
        return text;
    }
}
