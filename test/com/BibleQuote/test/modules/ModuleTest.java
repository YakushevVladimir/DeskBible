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

package com.BibleQuote.test.modules;

import com.BibleQuote.modules.VersificationMap;
import com.BibleQuote.modules.Module;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: Vladimir
 * Date: 16.02.13
 * Time: 0:47
 */
public class ModuleTest {
    @Test
    public void testGetLanguage() throws Exception {
        Module mod = new Module() {
            @Override
            public String getID() {
                return null;
            }

            @Override
            public String getDataSourceID() {
                return null;
            }

            @Override
            public VersificationMap getVersificationMap() {
                return null;
            }
		  };
        mod.language = null;
        Assert.assertEquals(mod.getLanguage(), "ru");
        mod.language = "ru-RU";
        Assert.assertEquals(mod.getLanguage(), "ru");
        mod.language = "ru_RU";
        Assert.assertEquals(mod.getLanguage(), "ru");
        mod.language = "en-US";
        Assert.assertEquals(mod.getLanguage(), "en");
    }
}
