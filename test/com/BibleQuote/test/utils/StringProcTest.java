/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.test.utils;

import junit.framework.Assert;
import org.junit.Test;

import com.BibleQuote.utils.StringProc;

/**
 * User: Vladimir
 * Date: 12.12.12
 * Time: 14:33
 */
public class StringProcTest {
    private String baseStr = "<h2><p>1 В первый 0259 год 08141 <script>function Script()</script>Кира 03566, <i>царя 04428 Персидского 06539</i>, во исполнение 03615 08800 слова 01697 Господня 03068 из уст 06310 Иеремии";

    @Test
    public void testStripTags() throws Exception {
        String testStr = "1 В первый 0259 год 08141 Кира 03566, царя 04428 Персидского 06539, во исполнение 03615 08800 слова 01697 Господня 03068 из уст 06310 Иеремии";
        Assert.assertEquals(StringProc.stripTags(baseStr), testStr);

        testStr = "<h2><p>1 В первый 0259 год 08141 Кира 03566, <i>царя 04428 Персидского 06539</i>, во исполнение 03615 08800 слова 01697 Господня 03068 из уст 06310 Иеремии";
        Assert.assertEquals(StringProc.stripTags(baseStr, "(p)|(/p)|(b)|(/b)|(i)|(/i)|(h2)|(/h2)"), testStr);
    }

    @Test
    public void testCleanStrongNumbers() throws Exception {
        String testStr = "<h2><p>1 В первый год <script>function Script()</script>Кира, <i>царя Персидского</i>, во исполнение слова Господня из уст Иеремии";
        Assert.assertEquals(StringProc.cleanStrongNumbers(baseStr), testStr);
    }

    @Test
    public void testCleanVerseNumbers() throws Exception {
        String testStr = "В первый год Кира, царя Персидского, во исполнение слова Господня из уст Иеремии";
        Assert.assertEquals(StringProc.cleanVerseNumbers(StringProc.cleanVerseText(baseStr)), testStr);
    }

    @Test
    public void testCleanVerseText() throws Exception {

    }
}
